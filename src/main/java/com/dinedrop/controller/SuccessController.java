package com.dinedrop.controller;

import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import com.dinedrop.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SuccessController {

    @Autowired
    private OrderServiceImpl orderService;

    @GetMapping("/success")
    public String successPage(@RequestParam(name = "session_id", required = false) String sessionId,
                              Model model) {
        // Fetch order using Stripe session_id
        Order order = orderService.getOrderBySessionId(sessionId);

        if (order != null) {
            User user = order.getUser();
            model.addAttribute("user", user);
            model.addAttribute("order", order);
        } else {
            // Fallback if no order found
            model.addAttribute("errorMessage", "Order not found for session: " + sessionId);
        }

        return "success";
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "cancel";
    }
}