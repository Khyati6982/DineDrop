package com.dinedrop.service;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.MenuItem;
import com.dinedrop.model.User;

import java.util.List;

public interface CartService {

    // ➕ Add item to cart
    void addToCart(User user, MenuItem menuItem, int quantity);

    // ➖ Remove item from cart
    void removeFromCart(User user, Long menuItemId);

    // 📦 Get all cart items for user
    List<CartItem> getCartItems(User user);

    // 💰 Calculate total price for user's cart
    double getTotalPrice(User user);

    // 🧹 Clear entire cart
    void clearCart(User user);

    // 🔄 Update quantity of a specific item
    void updateQuantity(User user, Long menuItemId, int quantity);
}
