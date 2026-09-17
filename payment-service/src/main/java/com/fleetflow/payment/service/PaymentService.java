package com.fleetflow.payment.service;

import com.fleetflow.payment.domain.entity.Payment;
import com.fleetflow.payment.domain.entity.Refund;
import com.fleetflow.payment.domain.enums.PaymentStatus;
import com.fleetflow.payment.dto.PaymentRequest;
import com.fleetflow.payment.dto.PaymentResponse;
import com.fleetflow.payment.dto.RefundRequest;
import com.fleetflow.payment.repository.PaymentRepository;
import com.fleetflow.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final ExternalPaymentGatewayClient gatewayClient;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Idempotency Check: if a payment for this booking already exists, return it
        Optional<Payment> existingPayment = paymentRepository.findByBookingId(request.getBookingId());
        if (existingPayment.isPresent()) {
            return mapToResponse(existingPayment.get());
        }

        String ref = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Call the external gateway, protected by Resilience4j
        boolean success = gatewayClient.chargeCard(ref, request.getAmount().doubleValue());
        
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .paymentReference(ref)
                .bookingId(request.getBookingId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(status)
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    @Transactional
    public PaymentResponse processRefund(RefundRequest request) {
        Payment payment = paymentRepository.findByBookingId(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Payment not found for booking ID: " + request.getBookingId()));

        // Idempotency: don't refund if already refunded or if the payment failed originally
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return mapToResponse(payment); // already refunded
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Cannot refund a payment that was not successful.");
        }

        Refund refund = Refund.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .reason(request.getReason())
                .status(PaymentStatus.SUCCESS) // Marking the refund transaction itself as successful
                .build();

        refundRepository.save(refund);

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment updated = paymentRepository.save(payment);

        return mapToResponse(updated);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .build();
    }
}
