package com.fleetflow.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.common.events.BaseEvent;
import com.fleetflow.common.events.BookingCancelledEvent;
import com.fleetflow.common.events.BookingConfirmedEvent;
import com.fleetflow.notification.domain.entity.NotificationLog;
import com.fleetflow.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationLogRepository notificationLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "booking-events", groupId = "notification-service-group")
    public void listen(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            
            if ("BookingConfirmedEvent".equals(event.getEventType())) {
                BookingConfirmedEvent confirmedEvent = objectMapper.readValue(message, BookingConfirmedEvent.class);
                handleBookingConfirmed(confirmedEvent);
            } else if ("BookingCancelledEvent".equals(event.getEventType())) {
                BookingCancelledEvent cancelledEvent = objectMapper.readValue(message, BookingCancelledEvent.class);
                handleBookingCancelled(cancelledEvent);
            }
        } catch (Exception e) {
            log.error("Failed to process notification event", e);
        }
    }

    private void handleBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Sending confirmation email/SMS for booking: {}", event.getBookingId());
        
        String msg = String.format("Your booking %s has been confirmed! Get ready for your ride.", event.getBookingId());
        
        NotificationLog logEntry = NotificationLog.builder()
                .bookingId(event.getBookingId())
                .notificationType("CONFIRMATION")
                .recipient("customer@example.com") // In reality, fetch from User Service
                .message(msg)
                .build();
                
        notificationLogRepository.save(logEntry);
        log.info("Notification log saved.");
    }

    private void handleBookingCancelled(BookingCancelledEvent event) {
        log.info("Sending cancellation email/SMS for booking: {}", event.getBookingId());
        
        String msg = String.format("Your booking %s has been cancelled. Reason: %s", 
                event.getBookingId(), event.getReason());
        
        NotificationLog logEntry = NotificationLog.builder()
                .bookingId(event.getBookingId())
                .notificationType("CANCELLATION")
                .recipient("customer@example.com") 
                .message(msg)
                .build();
                
        notificationLogRepository.save(logEntry);
        log.info("Notification log saved.");
    }
}
