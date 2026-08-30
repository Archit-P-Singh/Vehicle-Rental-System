package com.fleetflow.payment.controller;

import com.fleetflow.payment.dto.PaymentRequest;
import com.fleetflow.payment.dto.PaymentResponse;
import com.fleetflow.payment.dto.RefundRequest;
import com.fleetflow.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> processRefund(@Valid @RequestBody RefundRequest request) {
        PaymentResponse response = paymentService.processRefund(request);
        return ResponseEntity.ok(response);
    }
}
