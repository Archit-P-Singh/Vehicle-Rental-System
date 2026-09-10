package com.fleetflow.vehicle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.common.events.BaseEvent;
import com.fleetflow.common.events.BookingCreatedEvent;
import com.fleetflow.common.events.CompensateVehicleEvent;
import com.fleetflow.common.events.VehicleReservationFailedEvent;
import com.fleetflow.common.events.VehicleReservedEvent;
import com.fleetflow.vehicle.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleEventListener {

    private final VehicleService vehicleService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "vehicle-commands", groupId = "vehicle-service-group")
    public void listen(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("BookingCreatedEvent".equals(event.getEventType())) {
                handleBookingCreated(objectMapper.readValue(message, BookingCreatedEvent.class));
            } else if ("CompensateVehicleEvent".equals(event.getEventType())) {
                handleCompensateVehicle(objectMapper.readValue(message, CompensateVehicleEvent.class));
            }
        } catch (Exception e) {
            log.error("Failed to process vehicle command", e);
        }
    }

    private void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received BookingCreatedEvent for booking {}", event.getBookingId());
        try {
            ReservationRequest request = new ReservationRequest();
            request.setVehicleId(event.getVehicleId());
            request.setBookingId(event.getBookingId());
            request.setStartTime(event.getStartTime());
            request.setEndTime(event.getEndTime());
            
            vehicleService.reserveVehicle(request);
            
            VehicleReservedEvent successEvent = new VehicleReservedEvent(event.getBookingId(), event.getVehicleId());
            kafkaTemplate.send("vehicle-events", event.getBookingId(), successEvent);
            log.info("Successfully reserved vehicle and sent VehicleReservedEvent");
        } catch (Exception e) {
            log.error("Failed to reserve vehicle for booking {}", event.getBookingId(), e);
            VehicleReservationFailedEvent failedEvent = new VehicleReservationFailedEvent(
                    event.getBookingId(), event.getVehicleId(), e.getMessage());
            kafkaTemplate.send("vehicle-events", event.getBookingId(), failedEvent);
        }
    }

    private void handleCompensateVehicle(CompensateVehicleEvent event) {
        log.info("Received CompensateVehicleEvent for booking {}", event.getBookingId());
        try {
            vehicleService.releaseVehicle(event.getBookingId());
            log.info("Successfully compensated vehicle reservation for booking {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to compensate vehicle for booking {}", event.getBookingId(), e);
        }
    }
}
