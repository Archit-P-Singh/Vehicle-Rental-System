package com.fleetflow.booking.domain.entity;

import com.fleetflow.booking.domain.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String bookingNumber;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String vehicleId;

    @Column(nullable = false, length = 100)
    private String pickupLocation;

    @Column(nullable = false, length = 100)
    private String dropoffLocation;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BookingStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 10)
    private String currency;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
