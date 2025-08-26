package com.dinedrop.controller;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import com.dinedrop.service.CartService;
import com.dinedrop.service.OrderService;
import com.dinedrop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // ✅ Show checkout page
    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        double totalAmount = cartService.getTotalPrice(user);

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "checkout";
    }

    // ✅ Place order
    @PostMapping("/place")
    public String placeOrder(
            @RequestParam List<Long> menuItemIds,
            @RequestParam List<Integer> quantities,
            @RequestParam String deliveryAddress,
            HttpSession session,
            Model model) {

        User user = userService.getLoggedInUser(session);
        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Order order = orderService.placeOrder(user, menuItemIds, quantities, deliveryAddress);

        model.addAttribute("order", order);
        model.addAttribute("user", user);

        return "order-confirmation";
    }
}



