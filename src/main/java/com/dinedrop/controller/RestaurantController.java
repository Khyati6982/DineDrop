package com.dinedrop.controller;

import com.dinedrop.model.MenuItem;
import com.dinedrop.model.Restaurant;
import com.dinedrop.service.MenuItemService;
import com.dinedrop.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RestaurantController {

    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(MenuItemService menuService, RestaurantService restaurantService) {
        this.menuItemService = menuService;
        this.restaurantService = restaurantService;
    }

    // Static homepage
    @GetMapping("/")
    public String showHome() {
        return "index";
    }

    // Restaurant listing page
    @GetMapping("/restaurants")
    public String showAllRestaurants(Model model, HttpSession session) {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("user", session.getAttribute("user"));
        return "restaurants";
    }

    // Individual restaurant menu page
    @GetMapping("/restaurant/{id}/menu")
    public String showMenu(@PathVariable Long id, Model model, HttpSession session) {
        Restaurant restaurant = restaurantService.getRestaurantById(id); 
        List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(id);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("user", session.getAttribute("user"));

        return "menu";
    }
}
