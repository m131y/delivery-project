package org.example.delivery.order.dto;

import lombok.Builder;
import lombok.Data;
import org.example.delivery.order.entity.OrderStatus;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long count;
    private Long totalPrice;
    private String address;
    private OrderStatus orderStatus;
}
