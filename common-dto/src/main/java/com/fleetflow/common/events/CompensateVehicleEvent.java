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
public class CompensateVehicleEvent extends BaseEvent {
    private String bookingId;

    public CompensateVehicleEvent(String bookingId) {
        super();
        this.setEventType("CompensateVehicleEvent");
        this.bookingId = bookingId;
    }
}
