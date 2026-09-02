package com.fleetflow.booking.dto;

import com.fleetflow.booking.domain.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String id;
    private String bookingNumber;
    private String customerId;
    private String vehicleId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String currency;
}
