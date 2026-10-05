# Auth Service 🔐

The Auth Service handles Identity and Access Management for the platform.

## Key Responsibilities
- **User Management**: Registers new customers and stores their credentials securely using Spring Security and BCrypt password hashing.
- **Authentication**: Validates user logins and generates stateless JSON Web Tokens (JWT) using the `jjwt` library.
- **Database**: Owns the `fleetflow_auth` MySQL schema.
