package com.dinedrop.controller;

import com.dinedrop.model.User;
import com.dinedrop.service.CartItemService;
import com.dinedrop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartViewController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("cartItems", cartItemService.getCartItems(user));
        model.addAttribute("totalPrice", cartItemService.getCartTotal(user));
        return "cart";
    }

    @PostMapping("/cart/update/{menuItemId}")
    public String updateQuantity(@PathVariable Long menuItemId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartItemService.updateQuantity(user, menuItemId, quantity);
        redirectAttributes.addFlashAttribute("message", "Quantity updated.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{menuItemId}")
    public String removeItem(@PathVariable Long menuItemId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartItemService.removeFromCart(user, menuItemId);
        redirectAttributes.addFlashAttribute("message", "Item removed from cart.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartItemService.clearCart(user);
        redirectAttributes.addFlashAttribute("message", "Cart cleared.");
        return "redirect:/restaurants";
    }

    @PostMapping("/cart/add/{menuItemId}")
    public String addToCart(@PathVariable Long menuItemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartItemService.addToCart(user, menuItemId, quantity);
        redirectAttributes.addFlashAttribute("message", "Item added to cart.");
        return "redirect:/cart";
    }
}