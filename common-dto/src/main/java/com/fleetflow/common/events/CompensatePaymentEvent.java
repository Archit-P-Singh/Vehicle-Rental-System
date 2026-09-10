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
public class CompensatePaymentEvent extends BaseEvent {
    private String bookingId;
    private String reason;

    public CompensatePaymentEvent(String bookingId, String reason) {
        super();
        this.setEventType("CompensatePaymentEvent");
        this.bookingId = bookingId;
        this.reason = reason;
    }
}
