package com.fleetflow.vehicle.controller;

import com.fleetflow.vehicle.dto.ReservationRequest;
import com.fleetflow.vehicle.dto.VehicleRequest;
import com.fleetflow.vehicle.dto.VehicleResponse;
import com.fleetflow.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // Admin/Manager endpoint
    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(@Valid @RequestBody VehicleRequest request) {
        return new ResponseEntity<>(vehicleService.addVehicle(request), HttpStatus.CREATED);
    }

    // Customer endpoint
    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponse>> getAvailableVehicles() {
        return ResponseEntity.ok(vehicleService.getAvailableVehicles());
    }

    // This would typically be called via internal API or Kafka, but we expose it for testing Phase 3
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveVehicle(@Valid @RequestBody ReservationRequest request) {
        vehicleService.reserveVehicle(request);
        return ResponseEntity.ok("Vehicle reserved successfully!");
    }

    @PostMapping("/release/{bookingId}")
    public ResponseEntity<String> releaseVehicle(@PathVariable String bookingId) {
        vehicleService.releaseVehicle(bookingId);
        return ResponseEntity.ok("Vehicle released successfully!");
    }
}
