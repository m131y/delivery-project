package org.example.delivery.order.repository;

import org.example.delivery.order.entity.Order;
import org.example.delivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByUser(User user);
    List<Order> findAllByMenu_User_Username(String username);
}
