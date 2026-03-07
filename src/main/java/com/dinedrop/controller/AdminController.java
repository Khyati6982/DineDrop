package com.dinedrop.controller;

import com.dinedrop.model.Restaurant;
import com.dinedrop.model.MenuItem;
import com.dinedrop.service.RestaurantService;
import com.dinedrop.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemService menuItemService;

    // View all restaurants
    @GetMapping("/admin/restaurants")
    public String viewRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/restaurants";
    }

    // Add restaurant
    @GetMapping("/admin/restaurants/add")
    public String showAddForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "admin/add_restaurant";
    }

    @PostMapping("/admin/restaurants/add")
    public String addRestaurant(@ModelAttribute("restaurant") Restaurant restaurant) {
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/admin/restaurants";
    }

    // Edit restaurant
    @GetMapping("/admin/restaurants/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        model.addAttribute("restaurant", restaurant);
        return "admin/edit_restaurant";
    }

    @PostMapping("/admin/restaurants/edit/{id}")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute("restaurant") Restaurant updated) {
        restaurantService.updateRestaurant(id, updated);
        return "redirect:/admin/restaurants";
    }

    // Delete restaurant
    @GetMapping("/admin/restaurants/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }

    // View menus for a specific restaurant
    @GetMapping("/admin/restaurants/{id}/menus")
    public String viewRestaurantMenus(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return "redirect:/admin/restaurants";
        }
        List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(id);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        return "admin/restaurant_menu_list";
    }

    // View all menus across restaurants
    @GetMapping("/admin/menus")
    public String viewAllMenus(Model model) {
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        return "admin/menu_list";
    }
}