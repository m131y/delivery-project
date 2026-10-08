package org.example.delivery.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.delivery.payment.dto.PaymentRequest;
import org.example.delivery.payment.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    //결제 요청
    @PostMapping("/{orderId}")
    public void processPayment(@AuthenticationPrincipal String username, @PathVariable Long orderId, @RequestBody @Valid PaymentRequest paymentRequest) {
        paymentService.processPayment(username, orderId, paymentRequest);
    }

}
