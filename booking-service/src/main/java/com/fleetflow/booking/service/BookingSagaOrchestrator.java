package com.fleetflow.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.booking.domain.entity.Booking;
import com.fleetflow.booking.domain.entity.BookingSaga;
import com.fleetflow.booking.domain.entity.OutboxEvent;
import com.fleetflow.booking.domain.enums.BookingStatus;
import com.fleetflow.booking.domain.enums.SagaStatus;
import com.fleetflow.booking.domain.enums.SagaStep;
import com.fleetflow.booking.repository.BookingRepository;
import com.fleetflow.booking.repository.BookingSagaRepository;
import com.fleetflow.booking.repository.OutboxEventRepository;
import com.fleetflow.common.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingSagaOrchestrator {

    private final BookingRepository bookingRepository;
    private final BookingSagaRepository sagaRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    // Topics
    private static final String VEHICLE_COMMANDS = "vehicle-commands";
    private static final String PRICING_COMMANDS = "pricing-commands";
    private static final String PAYMENT_COMMANDS = "payment-commands";
    private static final String BOOKING_EVENTS = "booking-events";

    /**
     * Entry point of the saga. Called by BookingService after saving PENDING booking.
     */
    public void startSaga(Booking booking, BookingSaga saga) {
        log.info("Starting saga for booking {}", booking.getId());
        
        saga.setCurrentStep(SagaStep.RESERVE_VEHICLE);
        saga.setStatus(SagaStatus.VEHICLE_RESERVATION_PENDING);
        sagaRepository.save(saga);

        booking.setStatus(BookingStatus.VEHICLE_RESERVATION_PENDING);
        bookingRepository.save(booking);

        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId(),
                booking.getCustomerId(),
                booking.getVehicleId(),
                booking.getStartTime(),
                booking.getEndTime()
        );
        
        publishEventToOutbox(VEHICLE_COMMANDS, booking.getId(), event);
    }

    @KafkaListener(topics = "vehicle-events", groupId = "booking-orchestrator-group")
    @Transactional
    public void handleVehicleEvents(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("VehicleReservedEvent".equals(event.getEventType())) {
                VehicleReservedEvent vre = objectMapper.readValue(message, VehicleReservedEvent.class);
                handleVehicleReserved(vre);
            } else if ("VehicleReservationFailedEvent".equals(event.getEventType())) {
                VehicleReservationFailedEvent vrfe = objectMapper.readValue(message, VehicleReservationFailedEvent.class);
                handleVehicleReservationFailed(vrfe);
            }
        } catch (Exception e) {
            log.error("Error processing vehicle event", e);
        }
    }

    private void handleVehicleReserved(VehicleReservedEvent event) {
        log.info("Vehicle reserved for booking {}", event.getBookingId());
        BookingSaga saga = getSaga(event.getBookingId());
        Booking booking = getBooking(event.getBookingId());

        saga.setCurrentStep(SagaStep.CALCULATE_PRICE);
        saga.setStatus(SagaStatus.PRICING_PENDING);
        sagaRepository.save(saga);

        booking.setStatus(BookingStatus.PRICING_PENDING);
        bookingRepository.save(booking);

        CalculatePriceEvent cmd = new CalculatePriceEvent(
                booking.getId(),
                booking.getVehicleId(),
                booking.getStartTime(),
                booking.getEndTime()
        );
        publishEventToOutbox(PRICING_COMMANDS, booking.getId(), cmd);
    }

    private void handleVehicleReservationFailed(VehicleReservationFailedEvent event) {
        log.error("Vehicle reservation failed for booking {}: {}", event.getBookingId(), event.getReason());
        BookingSaga saga = getSaga(event.getBookingId());
        Booking booking = getBooking(event.getBookingId());

        saga.setStatus(SagaStatus.FAILED);
        sagaRepository.save(saga);

        booking.setStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);
        
        publishEventToOutbox(BOOKING_EVENTS, booking.getId(), new BookingCancelledEvent(booking.getId(), event.getReason()));
    }

    @KafkaListener(topics = "pricing-events", groupId = "booking-orchestrator-group")
    @Transactional
    public void handlePricingEvents(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("PriceCalculatedEvent".equals(event.getEventType())) {
                PriceCalculatedEvent pce = objectMapper.readValue(message, PriceCalculatedEvent.class);
                handlePriceCalculated(pce);
            } else if ("PricingFailedEvent".equals(event.getEventType())) {
                PricingFailedEvent pfe = objectMapper.readValue(message, PricingFailedEvent.class);
                handlePricingFailed(pfe);
            }
        } catch (Exception e) {
            log.error("Error processing pricing event", e);
        }
    }

    private void handlePriceCalculated(PriceCalculatedEvent event) {
        log.info("Price calculated for booking {}: {}", event.getBookingId(), event.getTotalAmount());
        BookingSaga saga = getSaga(event.getBookingId());
        Booking booking = getBooking(event.getBookingId());

        booking.setTotalAmount(event.getTotalAmount());
        booking.setCurrency(event.getCurrency());
        booking.setStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        saga.setCurrentStep(SagaStep.PROCESS_PAYMENT);
        saga.setStatus(SagaStatus.PAYMENT_PENDING);
        sagaRepository.save(saga);

        ProcessPaymentEvent cmd = new ProcessPaymentEvent(
                booking.getId(),
                booking.getCustomerId(),
                event.getTotalAmount(),
                event.getCurrency()
        );
        publishEventToOutbox(PAYMENT_COMMANDS, booking.getId(), cmd);
    }

    private void handlePricingFailed(PricingFailedEvent event) {
        log.error("Pricing failed for booking {}: {}", event.getBookingId(), event.getReason());
        // Compensate vehicle
        compensateVehicle(event.getBookingId(), event.getReason());
    }

    @KafkaListener(topics = "payment-events", groupId = "booking-orchestrator-group")
    @Transactional
    public void handlePaymentEvents(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("PaymentProcessedEvent".equals(event.getEventType())) {
                PaymentProcessedEvent ppe = objectMapper.readValue(message, PaymentProcessedEvent.class);
                handlePaymentProcessed(ppe);
            } else if ("PaymentFailedEvent".equals(event.getEventType())) {
                PaymentFailedEvent pfe = objectMapper.readValue(message, PaymentFailedEvent.class);
                handlePaymentFailed(pfe);
            }
        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }

    private void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Payment processed for booking {}", event.getBookingId());
        BookingSaga saga = getSaga(event.getBookingId());
        Booking booking = getBooking(event.getBookingId());

        saga.setCurrentStep(SagaStep.CONFIRM_BOOKING);
        saga.setStatus(SagaStatus.COMPLETED);
        sagaRepository.save(saga);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        publishEventToOutbox(BOOKING_EVENTS, booking.getId(), new BookingConfirmedEvent(booking.getId()));
    }

    private void handlePaymentFailed(PaymentFailedEvent event) {
        log.error("Payment failed for booking {}: {}", event.getBookingId(), event.getReason());
        // Compensate vehicle (no need to compensate pricing since it's stateless calculation)
        compensateVehicle(event.getBookingId(), event.getReason());
    }

    private void compensateVehicle(String bookingId, String reason) {
        BookingSaga saga = getSaga(bookingId);
        Booking booking = getBooking(bookingId);

        saga.setStatus(SagaStatus.COMPENSATING);
        saga.setCurrentStep(SagaStep.COMPENSATE_VEHICLE);
        sagaRepository.save(saga);

        booking.setStatus(BookingStatus.CANCELLATION_PENDING);
        bookingRepository.save(booking);

        CompensateVehicleEvent cmd = new CompensateVehicleEvent(bookingId);
        publishEventToOutbox(VEHICLE_COMMANDS, bookingId, cmd);
    }

    private void publishEventToOutbox(String topic, String aggregateId, BaseEvent event) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(aggregateId)
                    .aggregateType(event.getEventType())
                    .topic(topic)
                    .payload(objectMapper.writeValueAsString(event))
                    .status("PENDING")
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for outbox", e);
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    private BookingSaga getSaga(String bookingId) {
        return sagaRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Saga not found for booking " + bookingId));
    }

    private Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
    }
}
