package com.dinedrop.repository;

import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByPaymentStatus(String paymentStatus);
}
