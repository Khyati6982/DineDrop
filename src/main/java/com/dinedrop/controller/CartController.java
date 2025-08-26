package com.dinedrop.controller;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.MenuItem;
import com.dinedrop.model.User;
import com.dinedrop.service.CartService;
import com.dinedrop.service.MenuItemService;
import com.dinedrop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Long menuItemId,
                                            HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body("User not logged in.");
        }

        MenuItem menuItem = menuItemService.findById(menuItemId);
        if (menuItem == null) {
            throw new RuntimeException("Menu item not found with ID: " + menuItemId);
        }

        // ✅ Stock check
        if (!menuItem.isInStock()) {
            return ResponseEntity.status(400).body("Item is out of stock.");
        }

        cartService.addToCart(user, menuItem, 1); 
        return ResponseEntity.ok("Item added to cart successfully.");
    }

    @DeleteMapping("/remove/{menuItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long menuItemId,
                                                 HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body("User not logged in.");
        }

        cartService.removeFromCart(user, menuItemId);
        return ResponseEntity.ok("Item removed from cart.");
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(cartService.getCartItems(user));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalPrice(HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(cartService.getTotalPrice(user));
    }

    @PostMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body("User not logged in.");
        }

        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared.");
    }

    @PostMapping("/update/{menuItemId}")
    public ResponseEntity<String> updateQuantity(@PathVariable Long menuItemId,
                                                 @RequestParam int quantity,
                                                 HttpSession session) {
        User user = userService.getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body("User not logged in.");
        }

        cartService.updateQuantity(user, menuItemId, quantity);
        return ResponseEntity.ok("Quantity updated.");
    }
}

