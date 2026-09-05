package com.fleetflow.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingCreatedEvent extends BaseEvent {
    private String bookingId;
    private String customerId;
    private String vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public BookingCreatedEvent(String bookingId, String customerId, String vehicleId, 
                               LocalDateTime startTime, LocalDateTime endTime) {
        super();
        this.setEventType("BookingCreatedEvent");
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
