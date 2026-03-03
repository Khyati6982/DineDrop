package com.dinedrop.service;

import com.dinedrop.model.Order;
import com.dinedrop.model.User;

import java.util.List;

public interface OrderService {

    // Place a new order
    Order placeOrder(Long userId, List<Long> menuItemIds, List<Integer> quantities, String deliveryAddress);

    // Overloaded method for convenience
    Order placeOrder(User user, List<Long> menuItemIds, List<Integer> quantities, String deliveryAddress);

    // Get all orders for a user
    List<Order> getOrdersByUser(Long userId);

    // Get order details
    Order getOrderDetails(Long orderId);

    // Get all orders
    List<Order> getAllOrders();

    // Method for Stripe: update payment status and save session ID
    void updatePaymentStatus(Long orderId, String status, String stripeSessionId);

    // Lookup order by Stripe session ID (needed for success page)
    Order getOrderBySessionId(String stripeSessionId);
}