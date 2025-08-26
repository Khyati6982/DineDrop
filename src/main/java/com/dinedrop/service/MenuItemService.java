package com.dinedrop.service;

import com.dinedrop.model.MenuItem;
import java.util.List;

public interface MenuItemService {

    List<MenuItem> getAllMenuItems();

    void saveMenuItem(MenuItem menuItem);

    MenuItem findById(Long id);

    List<MenuItem> getMenuItemsByRestaurant(Long restaurantId);

    void updateMenuItem(Long id, MenuItem updated);

    void deleteMenuItem(Long id);
}
