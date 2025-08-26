package com.dinedrop.service;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.MenuItem;
import com.dinedrop.model.User;
import com.dinedrop.repository.CartItemRepository;
import com.dinedrop.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // ✅ Add item to cart
    @Transactional
    public void addToCart(User user, Long menuItemId, int quantity) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);
        if (menuItem == null) return;

        CartItem existingItem = cartItemRepository.findByUserAndMenuItem(user, menuItem);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(menuItem, user, quantity);
            cartItemRepository.save(newItem);
        }
    }

    // ✅ Get all cart items for a user
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    // ✅ Remove item from cart
    @Transactional
    public void removeFromCart(User user, Long menuItemId) {
        cartItemRepository.deleteByUserAndMenuItemId(user, menuItemId);
    }

    // ✅ Clear entire cart
    @Transactional
    public void clearCart(User user) {
        List<CartItem> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }

    // ✅ Get total cart price
    public double getCartTotal(User user) {
        return cartItemRepository.findByUser(user).stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    // ✅ Update quantity
    @Transactional
    public void updateQuantity(User user, Long menuItemId, int quantity) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);
        if (menuItem == null) return;

        CartItem item = cartItemRepository.findByUserAndMenuItem(user, menuItem);
        if (item != null) {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }
}
