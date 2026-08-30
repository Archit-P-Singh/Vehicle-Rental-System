package com.fleetflow.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundRequest {
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}
