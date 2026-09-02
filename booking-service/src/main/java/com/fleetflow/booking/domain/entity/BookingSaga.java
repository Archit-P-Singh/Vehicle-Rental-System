package com.fleetflow.booking.domain.entity;

import com.fleetflow.booking.domain.enums.SagaStatus;
import com.fleetflow.booking.domain.enums.SagaStep;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_sagas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSaga {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true)
    private String bookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SagaStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SagaStatus status;

    @Column(length = 100)
    private String lastProcessedEvent;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
