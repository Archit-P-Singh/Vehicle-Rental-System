package com.fleetflow.vehicle.dto;

import com.fleetflow.vehicle.domain.enums.FuelType;
import com.fleetflow.vehicle.domain.enums.Transmission;
import com.fleetflow.vehicle.domain.enums.VehicleCategory;
import com.fleetflow.vehicle.domain.enums.VehicleStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private String id;
    private String registrationNumber;
    private String brand;
    private String model;
    private VehicleCategory category;
    private FuelType fuelType;
    private Transmission transmission;
    private Integer manufactureYear;
    private String location;
    private VehicleStatus status;
    private String pricePlanId;
}
