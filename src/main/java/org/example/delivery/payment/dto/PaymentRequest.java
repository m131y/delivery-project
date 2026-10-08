package org.example.delivery.payment.dto;

import lombok.Data;
import org.example.delivery.payment.entity.PaymentMethod;

@Data
public class PaymentRequest {
    private PaymentMethod paymentMethod;
}
