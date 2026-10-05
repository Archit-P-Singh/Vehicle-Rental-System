# Notification Service 📬

The Notification Service acts as an asynchronous messaging hub to keep users informed without blocking core business flows.

## Key Responsibilities
- **Event-Driven Messaging**: Subscribes to the Kafka event streams (e.g., `BookingConfirmedEvent`, `BookingFailedEvent`) and triggers simulated Email or SMS dispatches to the customer.
- **Decoupling**: By placing this behind Kafka, the Booking Service never has to wait for a slow SMTP email server to respond.
- **Database**: Owns the `fleetflow_notification` MySQL schema for audit logging notification histories.
