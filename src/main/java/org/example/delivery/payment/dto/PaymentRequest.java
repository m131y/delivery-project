package org.example.delivery.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.delivery.payment.entity.PaymentMethod;

@Data
public class PaymentRequest {
    @NotNull(message =  "결제 수단은 필수 선택입니다.")
    private PaymentMethod paymentMethod;
}
