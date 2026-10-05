# Payment Service 💳

The Payment Service handles financial transactions and mock integrations with external third-party gateways (e.g., Stripe, PayPal).

## Key Responsibilities
- **Transaction Processing**: Consumes payment requests from Kafka, interacts with simulated external gateways, and publishes success or failure events back to the Booking Saga Orchestrator.
- **Resilience**: Network calls to external payment providers are inherently unreliable. This service wraps external HTTP calls using `Resilience4j`. It features a **Circuit Breaker** (to fail fast during outages and prevent thread exhaustion) and a **Retry** mechanism (for transient network blips).
- **Database**: Owns the `fleetflow_payment` MySQL schema to track transaction ledgers.
