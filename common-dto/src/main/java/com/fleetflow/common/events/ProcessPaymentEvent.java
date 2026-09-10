package com.fleetflow.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProcessPaymentEvent extends BaseEvent {
    private String bookingId;
    private String customerId;
    private BigDecimal amount;
    private String currency;

    public ProcessPaymentEvent(String bookingId, String customerId, BigDecimal amount, String currency) {
        super();
        this.setEventType("ProcessPaymentEvent");
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
    }
}
