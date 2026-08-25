CREATE TABLE price_plans (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    daily_rate DECIMAL(10, 2) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    insurance_rate_per_day DECIMAL(10, 2) NOT NULL,
    tax_percentage DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert a default pricing plan for easy testing later
INSERT INTO price_plans (id, name, daily_rate, hourly_rate, insurance_rate_per_day, tax_percentage)
VALUES ('default-plan-1', 'Standard Sedan', 1500.00, 150.00, 200.00, 18.00);
