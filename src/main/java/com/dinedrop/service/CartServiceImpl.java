package com.dinedrop.service;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.MenuItem;
import com.dinedrop.model.User;
import com.dinedrop.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public void addToCart(User user, MenuItem menuItem, int quantity) {
        // Get all items in the user's cart
        List<CartItem> existingItems = cartItemRepository.findByUser(user);

        if (!existingItems.isEmpty()) {
            // Check the restaurant of the first item in the cart
            Long existingRestaurantId = existingItems.get(0).getMenuItem().getRestaurant().getId();
            Long newRestaurantId = menuItem.getRestaurant().getId();

            // If restaurants differ, block addition
            if (!existingRestaurantId.equals(newRestaurantId)) {
                throw new IllegalArgumentException(
                    "You can only order from one restaurant at a time. Clear your cart to add items from another restaurant."
                );
            }
        }

        // If item already exists, update quantity
        CartItem existingItem = cartItemRepository.findByUserAndMenuItem(user, menuItem);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(menuItem, user, quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public void removeFromCart(User user, Long menuItemId) {
        CartItem item = cartItemRepository.findByUserAndMenuItemId(user, menuItemId);
        if (item != null) {
            cartItemRepository.delete(item);
        }
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Override
    public double getTotalPrice(User user) {
        return getCartItems(user).stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    @Override
    public void clearCart(User user) {
        List<CartItem> items = getCartItems(user);
        cartItemRepository.deleteAll(items);
    }

    @Override
    public void updateQuantity(User user, Long menuItemId, int quantity) {
        CartItem item = cartItemRepository.findByUserAndMenuItemId(user, menuItemId);
        if (item != null) {
            if (quantity <= 0) {
                cartItemRepository.delete(item); // Remove item if quantity is zero or negative
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }
    }

    @Override
    public CartItem getCartItemByMenuItemId(User user, Long menuItemId) {
        return cartItemRepository.findByUserAndMenuItemId(user, menuItemId);
    }
}