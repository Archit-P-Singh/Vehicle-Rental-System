CREATE TABLE bookings (
    id VARCHAR(36) PRIMARY KEY,
    booking_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    vehicle_id VARCHAR(36) NOT NULL,
    pickup_location VARCHAR(100) NOT NULL,
    dropoff_location VARCHAR(100) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2),
    currency VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE booking_sagas (
    id VARCHAR(36) PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL UNIQUE,
    current_step VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_processed_event VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);
