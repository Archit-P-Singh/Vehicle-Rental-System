package com.fleetflow.pricing.service;

import com.fleetflow.pricing.domain.entity.PricePlan;
import com.fleetflow.pricing.dto.PriceQuoteRequest;
import com.fleetflow.pricing.dto.PriceQuoteResponse;
import com.fleetflow.pricing.repository.PricePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricePlanRepository pricePlanRepository;

    public PriceQuoteResponse calculateQuote(PriceQuoteRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Fetch plan, or fallback to default
        PricePlan plan;
        if (request.getPricePlanId() != null && !request.getPricePlanId().isBlank()) {
            plan = pricePlanRepository.findById(request.getPricePlanId())
                    .orElseGet(() -> pricePlanRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new RuntimeException("No pricing plans available!")));
        } else {
            plan = pricePlanRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No pricing plans available!"));
        }

        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        long totalHours = duration.toHours();
        
        // If it's less than an hour, charge for 1 hour minimum
        if (totalHours == 0 && duration.toMinutes() > 0) {
            totalHours = 1;
        }

        // Calculate days and remaining hours
        long days = totalHours / 24;
        long remainingHours = totalHours % 24;

        // Base Rental Calculation
        BigDecimal baseRental = plan.getDailyRate().multiply(BigDecimal.valueOf(days))
                .add(plan.getHourlyRate().multiply(BigDecimal.valueOf(remainingHours)));

        // Insurance Calculation (Charged per whole day, minimum 1 day)
        long insuranceDays = days + (remainingHours > 0 ? 1 : 0);
        if (insuranceDays == 0) insuranceDays = 1;
        
        BigDecimal insurance = plan.getInsuranceRatePerDay().multiply(BigDecimal.valueOf(insuranceDays));

        // Tax Calculation (base + insurance) * tax_percentage / 100
        BigDecimal taxableAmount = baseRental.add(insurance);
        BigDecimal tax = taxableAmount.multiply(plan.getTaxPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal additionalCharges = BigDecimal.ZERO;

        BigDecimal totalAmount = baseRental
                .add(insurance)
                .add(tax)
                .subtract(discount)
                .add(additionalCharges);

        return PriceQuoteResponse.builder()
                .quoteId(UUID.randomUUID().toString())
                .vehicleId(request.getVehicleId())
                .baseRental(baseRental)
                .insurance(insurance)
                .tax(tax)
                .discount(discount)
                .additionalCharges(additionalCharges)
                .totalAmount(totalAmount)
                .currency("INR") // Hardcoded for this project scope
                .build();
    }
}
