package com.fleetflow.booking.service;

import com.fleetflow.booking.domain.entity.Booking;
import com.fleetflow.booking.domain.entity.BookingSaga;
import com.fleetflow.booking.domain.enums.BookingStatus;
import com.fleetflow.booking.domain.enums.SagaStatus;
import com.fleetflow.booking.domain.enums.SagaStep;
import com.fleetflow.booking.dto.BookingResponse;
import com.fleetflow.booking.dto.CreateBookingRequest;
import com.fleetflow.booking.repository.BookingRepository;
import com.fleetflow.booking.repository.BookingSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSagaRepository sagaRepository;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Received request to create booking for customer: {} and vehicle: {}", 
                request.getCustomerId(), request.getVehicleId());

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // 1. Create the booking in PENDING state
        Booking booking = Booking.builder()
                .bookingNumber("BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerId(request.getCustomerId())
                .vehicleId(request.getVehicleId())
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 2. Initialize the Saga state
        BookingSaga saga = BookingSaga.builder()
                .bookingId(savedBooking.getId())
                .currentStep(SagaStep.CREATE_BOOKING)
                .status(SagaStatus.STARTED)
                .build();

        sagaRepository.save(saga);

        log.info("Booking created with ID: {}. Saga initialized.", savedBooking.getId());
        
        // TODO: In Phase 8, we will publish a Kafka event here to trigger the next step (Reserve Vehicle)

        return mapToResponse(savedBooking);
    }

    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getCustomerBookings(String customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Very basic cancellation for now. In a full system, this would trigger a cancellation saga.
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Update saga if exists
        sagaRepository.findByBookingId(booking.getId()).ifPresent(saga -> {
            saga.setStatus(SagaStatus.COMPENSATING);
            saga.setCurrentStep(SagaStep.COMPENSATE_BOOKING);
            sagaRepository.save(saga);
        });
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .customerId(booking.getCustomerId())
                .vehicleId(booking.getVehicleId())
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .currency(booking.getCurrency())
                .build();
    }
}
