package com.dinedrop.controller;

import com.dinedrop.model.Order;
import com.dinedrop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public Order placeOrder(
            @RequestParam Long userId,
            @RequestParam List<Long> menuItemIds,
            @RequestParam List<Integer> quantities,
            @RequestParam String deliveryAddress) {
        return orderService.placeOrder(userId, menuItemIds, quantities, deliveryAddress);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{orderId}")
    public Order getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }
}
