package com.fleetflow.pricing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.common.events.BaseEvent;
import com.fleetflow.common.events.CalculatePriceEvent;
import com.fleetflow.common.events.PriceCalculatedEvent;
import com.fleetflow.common.events.PricingFailedEvent;
import com.fleetflow.pricing.dto.PriceQuoteRequest;
import com.fleetflow.pricing.dto.PriceQuoteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingEventListener {

    private final PricingService pricingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "pricing-commands", groupId = "pricing-service-group")
    public void listen(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if ("CalculatePriceEvent".equals(event.getEventType())) {
                handleCalculatePrice(objectMapper.readValue(message, CalculatePriceEvent.class));
            }
        } catch (Exception e) {
            log.error("Failed to process pricing command", e);
        }
    }

    private void handleCalculatePrice(CalculatePriceEvent event) {
        log.info("Received CalculatePriceEvent for booking {}", event.getBookingId());
        try {
            PriceQuoteRequest request = new PriceQuoteRequest();
            request.setVehicleId(event.getVehicleId());
            request.setStartTime(event.getStartTime());
            request.setEndTime(event.getEndTime());
            
            PriceQuoteResponse quote = pricingService.calculateQuote(request);
            
            PriceCalculatedEvent successEvent = new PriceCalculatedEvent(
                    event.getBookingId(), quote.getTotalAmount(), quote.getCurrency());
            kafkaTemplate.send("pricing-events", event.getBookingId(), successEvent);
            log.info("Successfully calculated price and sent PriceCalculatedEvent");
        } catch (Exception e) {
            log.error("Failed to calculate price for booking {}", event.getBookingId(), e);
            PricingFailedEvent failedEvent = new PricingFailedEvent(event.getBookingId(), e.getMessage());
            kafkaTemplate.send("pricing-events", event.getBookingId(), failedEvent);
        }
    }
}
