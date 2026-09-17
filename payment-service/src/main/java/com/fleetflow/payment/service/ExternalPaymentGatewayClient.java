package com.fleetflow.payment.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class ExternalPaymentGatewayClient {

    private final Random random = new Random();

    @Value("${payment.simulation.failure-rate:0.30}")
    private double failureRate;

    @CircuitBreaker(name = "externalPaymentGateway", fallbackMethod = "fallbackCharge")
    @Retry(name = "externalPaymentGateway", fallbackMethod = "fallbackCharge")
    public boolean chargeCard(String reference, double amount) {
        log.info("Attempting to charge card for reference {}", reference);
        if (random.nextDouble() < failureRate) {
            log.error("Simulated external gateway error occurred!");
            throw new RuntimeException("External Gateway Error: Connection timed out or payment declined.");
        }
        log.info("Charge successful for reference {}", reference);
        return true;
    }

    public boolean fallbackCharge(String reference, double amount, Throwable t) {
        log.warn("Fallback triggered for {}. Reason: {}", reference, t.getMessage());
        // Fallback response when CircuitBreaker is OPEN or Retries are exhausted
        return false;
    }
}
