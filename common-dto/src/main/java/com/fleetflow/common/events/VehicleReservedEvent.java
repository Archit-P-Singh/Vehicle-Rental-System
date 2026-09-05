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
public class VehicleReservedEvent extends BaseEvent {
    private String bookingId;
    private String vehicleId;
    
    public VehicleReservedEvent(String bookingId, String vehicleId) {
        super();
        this.setEventType("VehicleReservedEvent");
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
    }
}
