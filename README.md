# FleetFlow — Event-Driven Vehicle Rental & Fleet Management Platform

Event-driven vehicle rental and fleet management platform built with Java, Spring Boot, Kafka, MySQL and the Saga Pattern.

## Architecture

FleetFlow follows a microservice architecture built on Java 21 and Spring Boot 3.x. The platform uses a **Database-per-service** pattern where each service owns its data and communicates with other services through REST APIs and Kafka events.

### Microservices

* **API Gateway**: Entry point for all external traffic. Handles routing, rate limiting, and initial authentication checks.
* **Auth Service**: Manages user registration, authentication (JWT), roles, and driving license information.
* **Vehicle Service**: Manages the vehicle fleet, vehicle status, and vehicle reservations.
* **Booking Service**: The core business service. Manages the booking lifecycle and orchestrates the distributed Saga transaction.
* **Pricing Service**: Calculates rental prices based on vehicle, duration, and dynamically applied rules.
* **Payment Service**: Processes (simulated) payments and handles refunds.
* **Notification Service**: Listens for domain events and sends notifications to users.

### Distributed Transactions (Saga Pattern)

The creation of a booking involves multiple services. To ensure data consistency across the distributed system without relying on two-phase commits (2PC), FleetFlow uses **Saga Orchestration**.

The `Booking Service` acts as the orchestrator. The workflow is:
1. Create Booking (Pending)
2. Reserve Vehicle (Vehicle Service)
3. Calculate Price (Pricing Service)
4. Process Payment (Payment Service)
5. Confirm Booking
6. Send Notification

If any step fails, the orchestrator triggers compensating transactions (e.g., Refund Payment, Release Vehicle) to revert the system to a consistent state.

### Resilience and Event Delivery

* **Transactional Outbox Pattern**: Ensures domain events are reliably published to Kafka even if the message broker is temporarily unavailable.
* **Idempotent Consumers**: Protects against duplicate event delivery.
* **Optimistic Locking**: Prevents concurrent reservation of the same vehicle.
* **Resilience4j**: Handles retries, timeouts, and circuit breaking for synchronous REST calls.

## Technology Stack

* Java 21
* Spring Boot 3.x
* Spring Cloud Gateway
* Spring Security (JWT)
* Spring Data JPA / Hibernate
* MySQL 8
* Apache Kafka
* Redis
* Docker / Docker Compose
* JUnit 5 / Mockito / Testcontainers
* Prometheus / Grafana
