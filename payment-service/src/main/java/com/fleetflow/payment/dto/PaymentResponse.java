package com.fleetflow.payment.dto;

import com.fleetflow.payment.domain.enums.PaymentMethod;
import com.fleetflow.payment.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private String id;
    private String paymentReference;
    private String bookingId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
}
