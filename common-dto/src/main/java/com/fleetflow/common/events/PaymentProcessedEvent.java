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
public class PaymentProcessedEvent extends BaseEvent {
    private String bookingId;
    private String paymentId;
    
    public PaymentProcessedEvent(String bookingId, String paymentId) {
        super();
        this.setEventType("PaymentProcessedEvent");
        this.bookingId = bookingId;
        this.paymentId = paymentId;
    }
}
