package com.dinedrop.service;

import com.dinedrop.model.*;
import com.dinedrop.repository.*;
import com.dinedrop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Order placeOrder(Long userId, List<Long> menuItemIds, List<Integer> quantities, String deliveryAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTime(LocalDateTime.now());
        order.setPaymentStatus("PENDING"); // initially pending
        order.setItems(new ArrayList<>());

        double totalAmount = 0.0;

        for (int i = 0; i < menuItemIds.size(); i++) {
            MenuItem item = menuItemRepository.findById(menuItemIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            int quantity = quantities.get(i);
            double price = item.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(item);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(price);

            totalAmount += price * quantity;
            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Override
    public Order placeOrder(User user, List<Long> menuItemIds, List<Integer> quantities, String deliveryAddress) {
        return placeOrder(user.getId(), menuItemIds, quantities, deliveryAddress);
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }

    @Override
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Implementation for Stripe
    @Override
    public void updatePaymentStatus(Long orderId, String status, String stripeSessionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(status);
        order.setStripeSessionId(stripeSessionId);
        orderRepository.save(order);
    }
}
