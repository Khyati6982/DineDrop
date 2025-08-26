package com.dinedrop.controller;

import com.dinedrop.model.MenuItem;
import com.dinedrop.model.Restaurant;
import com.dinedrop.service.MenuItemService;
import com.dinedrop.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/menu")
    public String showMenu(@RequestParam Long restaurantId, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        return "menu";
    }
}

