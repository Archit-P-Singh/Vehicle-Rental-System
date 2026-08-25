package com.fleetflow.pricing.controller;

import com.fleetflow.pricing.dto.PriceQuoteRequest;
import com.fleetflow.pricing.dto.PriceQuoteResponse;
import com.fleetflow.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/quote")
    public ResponseEntity<PriceQuoteResponse> getQuote(@Valid @RequestBody PriceQuoteRequest request) {
        return ResponseEntity.ok(pricingService.calculateQuote(request));
    }
}
