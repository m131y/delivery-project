package org.example.delivery.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.delivery.global.error.ErrorCode;
import org.example.delivery.global.error.exception.BusinessException;
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
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new BusinessException(ErrorCode.CUSTOMER_ONLY, "고객님만 결제를 진행할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_OWNER, "본인의 주문만 결제할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.ORDERED) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_ALLOWED);
        }

        if (paymentRequest.getPaymentMethod() != PaymentMethod.CARD) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_PAYMENT_METHOD);
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
