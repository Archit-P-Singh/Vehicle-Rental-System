package com.fleetflow.pricing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuoteResponse {
    private String quoteId;
    private String vehicleId;
    private BigDecimal baseRental;
    private BigDecimal insurance;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal additionalCharges;
    private BigDecimal totalAmount;
    private String currency;
}
