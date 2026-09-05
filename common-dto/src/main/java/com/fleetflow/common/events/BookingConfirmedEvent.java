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
public class BookingConfirmedEvent extends BaseEvent {
    private String bookingId;
    
    public BookingConfirmedEvent(String bookingId) {
        super();
        this.setEventType("BookingConfirmedEvent");
        this.bookingId = bookingId;
    }
}
