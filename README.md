# FleetFlow: Distributed Car Rental Platform

FleetFlow is a cloud-native, microservices-based car rental platform built to demonstrate advanced enterprise software architecture. This project tackles complex distributed system challenges such as data consistency across disparate databases, network resilience, event-driven communication, and centralized observability.

## System Architecture

The application is decomposed into seven independent microservices, each owning its domain and data storage (Database-per-Service pattern). Communication between services is primarily asynchronous via Apache Kafka, ensuring high availability and loose coupling. 

### Microservices Overview

1. **API Gateway (`api-gateway`)**
   - **Role:** The single entry point for all client requests.
   - **Key Responsibilities:** Dynamic routing, centralized JWT token validation (Offloading authentication from downstream services), and distributed trace initiation (injecting `X-Correlation-Id`).
   - **Tech:** Spring Cloud Gateway, jjwt.

2. **Auth Service (`auth-service`)**
   - **Role:** Identity and Access Management.
   - **Key Responsibilities:** User registration, credential verification, and JWT generation.
   - **Tech:** Spring Security, MySQL.

3. **Vehicle Service (`vehicle-service`)**
   - **Role:** Fleet inventory management.
   - **Key Responsibilities:** CRUD operations for vehicles and querying availability.
   - **Tech:** Redis (Look-aside caching strategy for high-read availability queries), MySQL.

4. **Booking Service (`booking-service`)**
   - **Role:** Core business orchestrator.
   - **Key Responsibilities:** Manages the booking lifecycle. Acts as the **Saga Orchestrator** to coordinate distributed transactions. Implements the **Transactional Outbox Pattern** to guarantee reliable message delivery.
   - **Tech:** Apache Kafka, MySQL.

5. **Pricing Service (`pricing-service`)**
   - **Role:** Dynamic pricing engine.
   - **Key Responsibilities:** Calculates total booking costs based on temporal parameters, insurance rates, and taxes.

6. **Payment Service (`payment-service`)**
   - **Role:** Payment processing integration.
   - **Key Responsibilities:** Integrates with mock third-party payment gateways. Utilizes **Circuit Breaker** and **Retry** patterns to prevent cascading failures during external outages.
   - **Tech:** Resilience4j, MySQL.

7. **Notification Service (`notification-service`)**
   - **Role:** Asynchronous messaging.
   - **Key Responsibilities:** Consumes Kafka events to dispatch emails/SMS without blocking the main booking thread.

## Advanced Architectural Patterns Implemented

### 1. Saga Pattern (Orchestrated Distributed Transactions)
In a microservices architecture, a single business workflow (e.g., creating a booking) spans multiple services (Booking, Vehicle, Pricing, Payment). Two-Phase Commit (2PC) is unsuitable due to latency and locking overhead. 
- **Implementation:** FleetFlow uses a state-machine based **Saga Orchestrator** within the Booking Service. It emits command events (`ReserveVehicleCommand`, `ProcessPaymentCommand`) and listens for reply events. If any step fails (e.g., Payment fails), the orchestrator triggers compensating transactions (e.g., `ReleaseVehicleCommand`) to rollback the distributed state.

### 2. Transactional Outbox Pattern
When a microservice needs to update its database and publish a Kafka event simultaneously, a dual-write problem occurs. If the database commits but Kafka is down, the system is left in an inconsistent state.
- **Implementation:** The Booking Service writes the business entity (Booking) and the Event payload (OutboxEvent) to the database in a single, atomic, local ACID transaction. A separate asynchronous polling process reads the Outbox table and publishes the events to Kafka, guaranteeing *at-least-once* delivery semantics.

### 3. Idempotent Consumers
Due to Kafka's *at-least-once* delivery and potential network retries, consumers may receive the same event multiple times.
- **Implementation:** Consumers (e.g., the Saga Orchestrator) maintain a `processed_events` table. Before processing an incoming message, it checks if the unique `eventId` exists. If it does, the message is safely discarded, guaranteeing *exactly-once* processing semantics.

### 4. Circuit Breaker & Retry Mechanism
Microservices must be resilient to partial failures, especially when communicating with external systems.
- **Implementation:** The Payment Service wraps external gateway HTTP calls with `Resilience4j`. If the external system is slow or failing, the Circuit Breaker trips to the *OPEN* state, failing fast and preventing resource exhaustion (thread starvation). It periodically attempts a half-open state to check for recovery.

### 5. Distributed Tracing & Centralized Observability
Debugging a request that traverses a gateway and multiple asynchronous services is impossible without correlation.
- **Implementation:** 
  - **Tracing:** Spring Boot Actuator, Micrometer Tracing (Brave), and Zipkin are configured. A `traceId` is generated at the Gateway and propagated across HTTP headers and Kafka message headers, allowing full visualization of a request's journey.
  - **Metrics:** Micrometer Prometheus registry exposes `/actuator/prometheus` endpoints on all services, scraped periodically by a central Prometheus server.

## Getting Started

### Prerequisites
- Docker Engine
- Docker Compose

### Run Locally via Docker Compose
The entire infrastructure and application stack is containerized. To spin up MySQL, Kafka, Zookeeper, Redis, Zipkin, Prometheus, and all 7 microservices:

```bash
docker compose up --build -d
```

### Access Points
- **API Gateway**: `http://localhost:8080`
- **Zipkin UI (Tracing)**: `http://localhost:9411`
- **Prometheus UI (Metrics)**: `http://localhost:9090`

## Technology Stack

- **Core**: Java 21, Spring Boot 3
- **Data Persistence**: MySQL 8.0, Spring Data JPA, Flyway Migrations
- **Caching**: Redis 7.0, Spring Data Redis
- **Messaging**: Apache Kafka 7.4
- **Security**: Spring Security, JSON Web Tokens (jjwt)
- **Resilience**: Resilience4j
- **Observability**: Micrometer, Zipkin, Prometheus, Spring Boot Actuator
- **Testing**: JUnit 5, Mockito
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, Docker Compose

## Educational Context
This repository serves as a capstone project demonstrating mastery over distributed systems theory. It explicitly avoids monolith-first designs in favor of solving the inherent complexities of microservices (Network Fallacies, Eventual Consistency, Fault Tolerance) from day one.
