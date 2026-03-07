package com.dinedrop.controller;

import com.dinedrop.model.User;
import com.dinedrop.service.OrderServiceImpl;
import com.dinedrop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderViewController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrdersByUser(user.getId()));
        model.addAttribute("user", user);
        return "orders";
    }
}