package com.fleetflow.pricing.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PriceQuoteRequest {
    
    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;
    
    // We optionally accept pricePlanId if the booking service already knows it
    private String pricePlanId;
    
    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time cannot be in the past")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
}
