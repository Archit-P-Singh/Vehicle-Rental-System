package com.fleetflow.vehicle.repository;

import com.fleetflow.vehicle.domain.entity.VehicleReservation;
import com.fleetflow.vehicle.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, String> {
    
    @Query("SELECT vr FROM VehicleReservation vr WHERE vr.vehicleId = :vehicleId " +
           "AND vr.status IN ('RESERVED', 'ACTIVE') " +
           "AND ((vr.startTime <= :endTime AND vr.endTime >= :startTime))")
    List<VehicleReservation> findOverlappingReservations(
            @Param("vehicleId") String vehicleId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
            
    List<VehicleReservation> findByBookingId(String bookingId);
}
