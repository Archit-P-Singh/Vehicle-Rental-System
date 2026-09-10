package com.fleetflow.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.common.events.BaseEvent;
import com.fleetflow.common.events.CompensatePaymentEvent;
import com.fleetflow.common.events.PaymentFailedEvent;
import com.fleetflow.common.events.PaymentProcessedEvent;
import com.fleetflow.common.events.ProcessPaymentEvent;
import com.fleetflow.payment.domain.enums.PaymentMethod;
import com.fleetflow.payment.domain.enums.PaymentStatus;
import com.fleetflow.payment.dto.PaymentRequest;
import com.fleetflow.payment.dto.PaymentResponse;
import com.fleetflow.payment.dto.RefundRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-commands", groupId = "payment-service-group")
    public void listen(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("ProcessPaymentEvent".equals(event.getEventType())) {
                handleProcessPayment(objectMapper.readValue(message, ProcessPaymentEvent.class));
            } else if ("CompensatePaymentEvent".equals(event.getEventType())) {
                handleCompensatePayment(objectMapper.readValue(message, CompensatePaymentEvent.class));
            }
        } catch (Exception e) {
            log.error("Failed to process payment command", e);
        }
    }

    private void handleProcessPayment(ProcessPaymentEvent event) {
        log.info("Received ProcessPaymentEvent for booking {}", event.getBookingId());
        try {
            PaymentRequest request = new PaymentRequest();
            request.setBookingId(event.getBookingId());
            request.setCustomerId(event.getCustomerId());
            request.setAmount(event.getAmount());
            request.setCurrency(event.getCurrency());
            request.setPaymentMethod(PaymentMethod.CARD); // Defaulting for saga flow

            PaymentResponse response = paymentService.processPayment(request);
            
            if (response.getStatus() == PaymentStatus.SUCCESS) {
                PaymentProcessedEvent successEvent = new PaymentProcessedEvent(event.getBookingId(), response.getId());
                kafkaTemplate.send("payment-events", event.getBookingId(), successEvent);
                log.info("Successfully processed payment and sent PaymentProcessedEvent");
            } else {
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.getBookingId(), "Payment simulation failed randomly");
                kafkaTemplate.send("payment-events", event.getBookingId(), failedEvent);
                log.info("Payment failed simulation, sent PaymentFailedEvent");
            }
        } catch (Exception e) {
            log.error("Failed to process payment for booking {}", event.getBookingId(), e);
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.getBookingId(), e.getMessage());
            kafkaTemplate.send("payment-events", event.getBookingId(), failedEvent);
        }
    }

    private void handleCompensatePayment(CompensatePaymentEvent event) {
        log.info("Received CompensatePaymentEvent for booking {}", event.getBookingId());
        try {
            RefundRequest request = new RefundRequest();
            request.setBookingId(event.getBookingId());
            request.setReason(event.getReason());
            
            paymentService.processRefund(request);
            log.info("Successfully compensated payment for booking {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to compensate payment for booking {}", event.getBookingId(), e);
        }
    }
}
