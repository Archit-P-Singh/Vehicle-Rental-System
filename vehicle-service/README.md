# Vehicle Service

The Vehicle Service manages the entire car fleet inventory and is built for high-throughput availability queries.

## Key Responsibilities
- **Inventory Management**: CRUD operations for adding, updating, and removing vehicles.
- **Availability**: Checks if a car is available for a given time period and handles locking it when a booking is made.
- **Caching**: Implements the Look-aside caching pattern using Redis. The `availableVehicles` catalog is cached with a TTL, and cache eviction occurs whenever a vehicle's state changes, drastically reducing database read pressure.
- **Concurrency**: Uses JPA Optimistic Locking (`@Version`) to prevent double-booking if two users try to rent the same car at the exact same millisecond.
- **Database**: Owns the `fleetflow_vehicle` MySQL schema and `fleetflow-redis`.
