package com.dinedrop.repository;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.MenuItem;
import com.dinedrop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    CartItem findByUserAndMenuItem(User user, MenuItem menuItem);

    void deleteByUserAndMenuItemId(User user, Long menuItemId);

    void deleteByUser(User user);
    
    CartItem findByUserAndMenuItemId(User user, Long menuItemId);
}
