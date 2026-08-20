package com.fleetflow.vehicle.dto;

import com.fleetflow.vehicle.domain.enums.FuelType;
import com.fleetflow.vehicle.domain.enums.Transmission;
import com.fleetflow.vehicle.domain.enums.VehicleCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {
    
    @NotBlank(message = "Registration number is required")
    private String registrationNumber;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Category is required")
    private VehicleCategory category;
    
    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;
    
    @NotNull(message = "Transmission is required")
    private Transmission transmission;
    
    @Min(value = 2000, message = "Manufacture year must be valid")
    private Integer manufactureYear;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String pricePlanId;
}
