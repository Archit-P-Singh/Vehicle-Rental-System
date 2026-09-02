package com.fleetflow.booking.repository;

import com.fleetflow.booking.domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    List<Booking> findByCustomerId(String customerId);
}
