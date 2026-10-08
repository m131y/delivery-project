package org.example.delivery.order.service;

import lombok.RequiredArgsConstructor;
import org.example.delivery.global.error.ErrorCode;
import org.example.delivery.global.error.exception.BusinessException;
import org.example.delivery.menu.entity.Menu;
import org.example.delivery.menu.repository.MenuRepository;
import org.example.delivery.order.dto.OrderRequest;
import org.example.delivery.order.dto.OrderResponse;
import org.example.delivery.order.entity.Order;
import org.example.delivery.order.entity.OrderStatus;
import org.example.delivery.order.repository.OrderRepository;
import org.example.delivery.user.entity.Role;
import org.example.delivery.user.entity.User;
import org.example.delivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public OrderResponse createOrder(String username, Long menuId, OrderRequest orderRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new BusinessException(ErrorCode.CUSTOMER_ONLY, "고객님만 메뉴를 주문할 수 있습니다.");
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_DELETED);
        }

        Order order = Order.builder()
                .count(orderRequest.getCount())
                .totalPrice(orderRequest.getCount()*menu.getPrice())
                .address(orderRequest.getAddress())
                .user(user)
                .menu(menu)
                .orderStatus(OrderStatus.ORDERED)
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        List<Order> orderList = new ArrayList<>();

        if (user.getRole() == Role.CUSTOMER) {
            // 고객: 내가 주문한 목록
            orderList = orderRepository.findAllByUser(user);

        } else if (user.getRole() == Role.OWNER){
            // 사장: 내 메뉴에 들어온 주문 목록
            orderList = orderRepository.findAllByMenu_User_Username(username);
        }

        return orderList.stream()
                .map(OrderResponse::from)
                .toList();
    }

    public void cancelOrder(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new BusinessException(ErrorCode.CUSTOMER_ONLY, "고객님만 메뉴를 취소할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_OWNER, "본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.ORDERED) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
    }

    public void changeOrderStatus(String username, Long orderId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        if (user.getRole() == Role.CUSTOMER) {
            throw new BusinessException(ErrorCode.OWNER_ONLY, "사장님만 상태를 변경할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.ORDER_NOT_FOUND)
                );

        if (!order.getMenu().getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_OWNER, "본인의 주문만 상태 변경할 수 있습니다.");
        }

        if (!(order.getOrderStatus() == OrderStatus.PAID
                || order.getOrderStatus() == OrderStatus.ACCEPTED)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_CHANGE_NOT_ALLOWED);
        }

        if (order.getOrderStatus() == OrderStatus.PAID) {
            order.setOrderStatus(OrderStatus.ACCEPTED);
        } else if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
            order.setOrderStatus(OrderStatus.COMPLETED);
        }
    }
}
