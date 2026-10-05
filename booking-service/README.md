# Booking Service

The Booking Service is the core orchestrator of the entire platform. It handles the complex lifecycle of a car rental booking.

## Key Responsibilities
- **Saga Orchestrator**: Since a booking requires inventory reservation, price calculation, and payment processing, this service acts as the central brain. It emits command events to Kafka and listens for success/failure replies to push the booking state forward or trigger compensating transactions (rollbacks).
- **Transactional Outbox**: To solve the dual-write problem (saving the booking to the DB while publishing to Kafka), it uses the Outbox pattern. A database transaction atomically saves both the booking and an `OutboxEvent`. A background scheduler polls the outbox table to publish events reliably.
- **Idempotency**: Implements a `processed_events` table to ensure that if Kafka delivers the same event twice, the orchestrator only processes it once.
- **Database**: Owns the `fleetflow_booking` MySQL schema.
