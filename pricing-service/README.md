# Pricing Service

The Pricing Service is a stateless engine responsible for dynamic quote calculation.

## Key Responsibilities
- **Dynamic Calculation**: Calculates total rental quotes based on base daily/hourly rates, insurance premiums, and tax percentages.
- **Event Consumption**: Listens for pricing calculation requests from the Booking Service via Kafka, calculates the total, and publishes the result back.
- **Database**: Owns the `fleetflow_pricing` MySQL schema, containing temporal pricing plans.
