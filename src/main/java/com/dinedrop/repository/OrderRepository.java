package com.dinedrop.repository;

import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByPaymentStatus(String paymentStatus);

    Optional<Order> findByStripeSessionId(String stripeSessionId);

    // Query to fetch orders with items, food items, and restaurants
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.menuItem m " +
           "JOIN FETCH m.restaurant r " +
           "WHERE o.user.id = :userId")
    List<Order> findOrdersWithDetailsByUserId(@Param("userId") Long userId);
}