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
public class PriceCalculatedEvent extends BaseEvent {
    private String bookingId;
    private BigDecimal totalAmount;
    private String currency;
    
    public PriceCalculatedEvent(String bookingId, BigDecimal totalAmount, String currency) {
        super();
        this.setEventType("PriceCalculatedEvent");
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }
}
