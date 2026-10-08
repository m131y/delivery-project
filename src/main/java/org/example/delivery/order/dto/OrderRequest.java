package org.example.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "수량은 필수 입력입니다.")
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Long count;
    @NotBlank(message = "배송 주소는 필수 입력입니다.")
    private String address;
}
