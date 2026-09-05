package com.fleetflow.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VehicleReservationFailedEvent extends BaseEvent {
    private String bookingId;
    private String vehicleId;
    private String reason;
    
    public VehicleReservationFailedEvent(String bookingId, String vehicleId, String reason) {
        super();
        this.setEventType("VehicleReservationFailedEvent");
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
        this.reason = reason;
    }
}
