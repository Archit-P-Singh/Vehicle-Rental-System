package com.fleetflow.vehicle.service;

import com.fleetflow.vehicle.domain.entity.Vehicle;
import com.fleetflow.vehicle.domain.entity.VehicleReservation;
import com.fleetflow.vehicle.domain.enums.ReservationStatus;
import com.fleetflow.vehicle.domain.enums.VehicleStatus;
import com.fleetflow.vehicle.dto.ReservationRequest;
import com.fleetflow.vehicle.dto.VehicleRequest;
import com.fleetflow.vehicle.dto.VehicleResponse;
import com.fleetflow.vehicle.repository.VehicleRepository;
import com.fleetflow.vehicle.repository.VehicleReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleReservationRepository reservationRepository;

    public VehicleResponse addVehicle(VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(request.getRegistrationNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .category(request.getCategory())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .manufactureYear(request.getManufactureYear())
                .location(request.getLocation())
                .pricePlanId(request.getPricePlanId())
                .status(VehicleStatus.AVAILABLE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    public List<VehicleResponse> getAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reserveVehicle(ReservationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available for reservation right now");
        }

        // Check overlapping reservations
        List<VehicleReservation> overlaps = reservationRepository.findOverlappingReservations(
                request.getVehicleId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Vehicle is already reserved for this timeframe!");
        }

        // We temporarily update vehicle status (this triggers the @Version increment for optimistic locking)
        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);

        VehicleReservation reservation = VehicleReservation.builder()
                .vehicleId(vehicle.getId())
                .bookingId(request.getBookingId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ReservationStatus.RESERVED)
                .build();

        reservationRepository.save(reservation);
    }

    @Transactional
    public void releaseVehicle(String bookingId) {
        List<VehicleReservation> reservations = reservationRepository.findByBookingId(bookingId);
        if (reservations.isEmpty()) {
            return; // Nothing to release
        }

        for (VehicleReservation res : reservations) {
            res.setStatus(ReservationStatus.RELEASED);
            reservationRepository.save(res);

            Vehicle vehicle = vehicleRepository.findById(res.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            // If it was reserved by this booking, make it available again
            if (vehicle.getStatus() == VehicleStatus.RESERVED) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        }
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .category(vehicle.getCategory())
                .fuelType(vehicle.getFuelType())
                .transmission(vehicle.getTransmission())
                .manufactureYear(vehicle.getManufactureYear())
                .location(vehicle.getLocation())
                .status(vehicle.getStatus())
                .pricePlanId(vehicle.getPricePlanId())
                .build();
    }
}
