package org.example.delivery.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.delivery.order.dto.OrderRequest;
import org.example.delivery.order.dto.OrderResponse;
import org.example.delivery.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping("/{menuId}")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal String username, @PathVariable Long menuId, @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(username, menuId, orderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(orderService.getOrders(username));
    }

    // 주문 취소 (customer)
    @PutMapping("/{orderId}")
    public void cancelOrder(@AuthenticationPrincipal String username, @PathVariable Long orderId) {
        orderService.cancelOrder(username, orderId);
    }
    // 주문 상태 변경 (owner)
    @PutMapping("/owner/{orderId}")
    public void changeOrder(@AuthenticationPrincipal String username, @PathVariable Long orderId) {
        orderService.changeOrder(username, orderId);
    }

}
