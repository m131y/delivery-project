package org.example.delivery.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.delivery.exception.ForbiddenException;
import org.example.delivery.order.entity.Order;
import org.example.delivery.order.entity.OrderStatus;
import org.example.delivery.order.repository.OrderRepository;
import org.example.delivery.payment.dto.PaymentRequest;
import org.example.delivery.payment.entity.Payment;
import org.example.delivery.payment.entity.PaymentMethod;
import org.example.delivery.payment.repository.PaymentRepository;
import org.example.delivery.user.entity.Role;
import org.example.delivery.user.entity.User;
import org.example.delivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public void processPayment(String username, Long orderId, PaymentRequest paymentRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new ForbiddenException("고객님만 결제를 진행할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        if (user != order.getUser()) {
            throw new ForbiddenException("본인의 주문만 결제할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.ORDERED) {
            throw new ForbiddenException("주문 요청 상태일 때만 결제가 가능합니다.");
        }

        if (paymentRequest.getPaymentMethod() != PaymentMethod.CARD) {
            throw new ForbiddenException("현재 결제는 카드만 가능합니다.");
        }

        Payment payment = Payment.builder()
                .order(order)
                .user(user)
                .paymentAmount(order.getTotalPrice())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .build();

        paymentRepository.save(payment);
        order.setOrderStatus(OrderStatus.PAID);
    }
}
