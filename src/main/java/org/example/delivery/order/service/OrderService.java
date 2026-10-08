package org.example.delivery.order.service;

import lombok.RequiredArgsConstructor;
import org.example.delivery.exception.ForbiddenException;
import org.example.delivery.exception.MenuNotFoundException;
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
                .orElseThrow(()->new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new ForbiddenException("고객님만 메뉴를 주문할 수 있습니다.");
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new MenuNotFoundException("메뉴를 찾을 수 없습니다."));

        if (menu.isDeleted()) {
            throw new ForbiddenException("삭제된 메뉴 입니다.");
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

        return OrderResponse.builder()
                .Id(savedOrder.getId())
                .count(savedOrder.getCount())
                .totalPrice(savedOrder.getTotalPrice())
                .address(savedOrder.getAddress())
                .orderStatus(savedOrder.getOrderStatus())
                .build();
    }

    public List<OrderResponse> getOrders(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("로그인 정보를 찾을 수 없습니다.")
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
                .map(order -> OrderResponse.builder()
                        .Id(order.getId())
                        .count(order.getCount())
                        .totalPrice(order.getTotalPrice())
                        .address(order.getAddress())
                        .orderStatus(order.getOrderStatus())
                        .build())
                .toList();
    }

    public void cancelOrder(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        if (!(user.getRole() == Role.CUSTOMER)) {
            throw new ForbiddenException("고객님만 메뉴를 취소할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        if (user != order.getUser()) {
            throw new ForbiddenException("본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.ORDERED) {
            throw new ForbiddenException("주문 요청 상태일 때만 취소가 가능합니다.");
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public void changeOrder(String username, Long orderId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("로그인 정보를 찾을 수 없습니다.")
                );

        if (user.getRole() == Role.CUSTOMER) {
            throw new ForbiddenException("사장님만 상태를 변경할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
                );

        if (!user.getId().equals(order.getMenu().getUser().getId())) {
            throw new ForbiddenException("본인의 주문만 상태 변경할 수 있습니다.");
        }

        if (!(order.getOrderStatus() == OrderStatus.PAID
                || order.getOrderStatus() == OrderStatus.ACCEPTED)) {
            throw new ForbiddenException("결제 완료, 주문 수락 상태일 때만 변경이 가능합니다.");
        }

        if (order.getOrderStatus() == OrderStatus.PAID) {
            order.setOrderStatus(OrderStatus.ACCEPTED);
        } else if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
            order.setOrderStatus(OrderStatus.COMPLETED);
        }

        orderRepository.save(order);
    }
}
