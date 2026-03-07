package com.dinedrop.controller;

import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import com.dinedrop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SuccessController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/success")
    public String successPage(@RequestParam(name = "session_id", required = false) String sessionId,
                              Model model) {
        if (sessionId == null || sessionId.isEmpty()) {
            model.addAttribute("errorMessage", "Missing session ID in request.");
            return "error_page";
        }

        Order order = orderService.getOrderBySessionId(sessionId);

        if (order != null) {
            User user = order.getUser();
            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("stripeSessionId", order.getStripeSessionId());
            model.addAttribute("stripePaymentId", order.getStripePaymentId());
            return "success";
        } else {
            model.addAttribute("errorMessage", "Order not found for session: " + sessionId);
            return "error_page";
        }
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "cancel";
    }
}