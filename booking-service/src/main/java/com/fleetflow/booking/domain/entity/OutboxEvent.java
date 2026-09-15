package com.fleetflow.booking.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false, length = 50)
    private String aggregateType;
    
    @Column(nullable = false, length = 100)
    private String topic;

    @Column(nullable = false, columnDefinition = "json")
    private String payload;

    @Column(nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
