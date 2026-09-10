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
public class CalculatePriceEvent extends BaseEvent {
    private String bookingId;
    private String vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public CalculatePriceEvent(String bookingId, String vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        super();
        this.setEventType("CalculatePriceEvent");
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
