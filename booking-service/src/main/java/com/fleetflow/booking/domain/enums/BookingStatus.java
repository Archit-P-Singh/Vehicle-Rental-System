package com.fleetflow.booking.domain.enums;

public enum BookingStatus {
    PENDING,
    VEHICLE_RESERVATION_PENDING,
    VEHICLE_RESERVED,
    PRICING_PENDING,
    PAYMENT_PENDING,
    CONFIRMED,
    CANCELLATION_PENDING,
    CANCELLED,
    COMPLETED,
    FAILED
}
