# API Gateway Service 🚦

The API Gateway is the single point of entry for the FleetFlow platform, built with Spring Cloud Gateway.

## Key Responsibilities
- **Routing**: Intercepts all incoming client requests and routes them to the appropriate backend microservices based on URL paths.
- **Security**: Acts as the first line of defense. It implements a global `AuthenticationFilter` that intercepts requests, validates the JWT token's signature, and extracts user claims to pass down as HTTP headers (`X-User-Id`, `X-User-Role`). Downstream services therefore don't need to reinvent authentication.
- **Tracing**: Injects a unique `X-Correlation-Id` into every request header for distributed tracing using Zipkin and Micrometer.
