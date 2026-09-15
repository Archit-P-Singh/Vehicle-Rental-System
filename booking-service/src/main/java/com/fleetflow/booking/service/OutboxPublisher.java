package com.fleetflow.booking.service;

import com.fleetflow.booking.domain.entity.OutboxEvent;
import com.fleetflow.booking.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.poll.interval:5000}")
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        
        if (pendingEvents.isEmpty()) {
            return;
        }
        
        log.info("Found {} pending outbox events. Publishing to Kafka...", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // Publish to Kafka
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
                
                // Mark as processed (or delete it to save space)
                event.setStatus("PROCESSED");
                outboxEventRepository.save(event);
                
                log.info("Successfully published outbox event {} to topic {}", event.getId(), event.getTopic());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                // In a production system, we'd add retry logic or a dead letter queue here
            }
        }
    }
}
