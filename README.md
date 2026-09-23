# FleetFlow: Distributed Car Rental Platform 🚗☁️

FleetFlow is a modern, cloud-native, microservices-based car rental platform. Built as a final year college project to demonstrate enterprise-grade software architecture, it features a robust event-driven design, distributed data patterns, API gateways, and complete observability.

## 🌟 Key Features
- **Event-Driven Architecture**: Fully decoupled microservices communicating asynchronously via Apache Kafka.
- **Saga Pattern**: Distributed transaction management using Orchestration to ensure data consistency across multiple databases.
- **Transactional Outbox & Idempotency**: Bulletproof guarantees against dual-write problems and exactly-once event processing guarantees.
- **Circuit Breakers & Retries**: `Resilience4j` implementation for graceful handling of external payment gateway failures.
- **Caching**: Blazing fast reads for vehicle availability using Redis.
- **Security**: Centralized JWT validation at the Spring Cloud Gateway.
- **Observability**: Distributed tracing and metrics via Micrometer, Zipkin, and Prometheus.

## 🏗️ Architecture

The system is decomposed into 7 distinct microservices:

1. **API Gateway (`api-gateway`)**: Spring Cloud Gateway. Handles routing, global JWT validation, rate limiting, and injects Correlation IDs for tracing.
2. **Auth Service (`auth-service`)**: User registration, login, and JWT token issuance.
3. **Vehicle Service (`vehicle-service`)**: Manages the fleet catalog, vehicle availability, and Redis caching.
4. **Booking Service (`booking-service`)**: The core orchestrator. Manages the booking lifecycle and drives the Saga workflow. Implements the Transactional Outbox pattern.
5. **Pricing Service (`pricing-service`)**: Calculates quotes, discounts, and taxes based on real-time plans.
6. **Payment Service (`payment-service`)**: Integrates with mock external gateways. Implements `Resilience4j` circuit breakers.
7. **Notification Service (`notification-service`)**: Listens to Kafka topics and sends asynchronous emails/SMS for booking confirmations.

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 & Maven 3.9+ (Optional, if building locally outside Docker)

### Run with Docker Compose
The entire infrastructure (MySQL, Kafka, Zookeeper, Redis, Zipkin, Prometheus) and all 7 microservices can be spun up with a single command:

```bash
docker compose up --build -d
```

### Accessing the System
- **API Gateway**: `http://localhost:8080`
- **Zipkin (Tracing)**: `http://localhost:9411`
- **Prometheus (Metrics)**: `http://localhost:9090`
- **Kafka UI** (if configured): `http://localhost:8080`

## 🛠️ Technology Stack
- **Backend Framework**: Spring Boot 3 (Java 21)
- **Databases**: MySQL 8.0, Redis 7.0
- **Message Broker**: Apache Kafka
- **Observability**: Micrometer, Zipkin, Prometheus, Spring Boot Actuator
- **Security**: Spring Security, JWT (jjwt)
- **Resilience**: Resilience4j (Circuit Breaker & Retry)
- **Containerization**: Docker, Docker Compose

## 🎓 About This Project
This project was built to learn and implement complex distributed systems patterns inspired by real-world enterprise architectures. It tackles hard problems like distributed transactions, network unreliability, and database caching.

*Developed as a Final Year Project.*
