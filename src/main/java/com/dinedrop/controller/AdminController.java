package com.dinedrop.controller;

import com.dinedrop.model.Restaurant;
import com.dinedrop.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/admin/restaurants")
    public String viewRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/restaurants";
    }

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

    @GetMapping("/admin/restaurants/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/admin/restaurant_menu_list")
    public String viewRestaurantMenuList() {
        return "admin/restaurant_menu_list";
    }
}
