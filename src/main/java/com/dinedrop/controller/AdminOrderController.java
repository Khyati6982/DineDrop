package com.dinedrop.controller;

import com.dinedrop.model.Order;
import com.dinedrop.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderServiceImpl orderService;

    // Show all orders
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "admin/orders"; 
    }

    // Update payment status (PAID, PENDING, FAILED)
    @PostMapping("/updatePayment")
    public String updatePaymentStatus(@RequestParam Long orderId,
                                      @RequestParam String paymentStatus) {
        Order order = orderService.findById(orderId);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            orderService.save(order);
        }
        return "redirect:/admin/orders";
    }

    // Update order lifecycle status (Pending, Delivered, Cancelled)
    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam String status) {
        Order order = orderService.findById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderService.save(order);
        }
        return "redirect:/admin/orders";
    }

    // Cancel order
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId) {
        Order order = orderService.findById(orderId);
        if (order != null) {
            order.setStatus("Cancelled");
            orderService.save(order);
        }
        return "redirect:/admin/orders";
    }

    // View order details
    @GetMapping("/details/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        if (order != null) {
            model.addAttribute("order", order);
            return "admin/order_details";
        }
        return "redirect:/admin/orders";
    }
}