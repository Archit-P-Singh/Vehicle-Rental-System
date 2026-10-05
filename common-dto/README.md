# Common DTOs

This library module contains shared Data Transfer Objects (DTOs) and Event Schemas.

## Key Responsibilities
- **Contract Management**: Provides a single source of truth for the Kafka event structures (e.g., `ReserveVehicleCommand`, `PaymentProcessedEvent`). 
- **Dependency**: Built as a standard JAR that all other microservices import as a Maven dependency to ensure strict type safety when serializing and deserializing JSON payloads over the network.
