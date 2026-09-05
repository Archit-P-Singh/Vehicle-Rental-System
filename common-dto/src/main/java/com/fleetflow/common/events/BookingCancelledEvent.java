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
public class BookingCancelledEvent extends BaseEvent {
    private String bookingId;
    private String reason;
    
    public BookingCancelledEvent(String bookingId, String reason) {
        super();
        this.setEventType("BookingCancelledEvent");
        this.bookingId = bookingId;
        this.reason = reason;
    }
}
