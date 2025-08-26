package com.dinedrop.controller;

import com.dinedrop.model.MenuItem;
import com.dinedrop.model.Restaurant;
import com.dinedrop.service.MenuItemService;
import com.dinedrop.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/restaurants/{restaurantId}/menu")
public class AdminRestaurantMenuController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemService menuItemService;

    // 🧾 List all menu items for a restaurant
    @GetMapping
    public String listMenuItems(@PathVariable Long restaurantId,
                                Model model,
                                @ModelAttribute("message") String message,
                                @ModelAttribute("error") String error) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return "redirect:/admin/restaurants?error=notfound";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItemService.getMenuItemsByRestaurant(restaurantId));
        return "admin/restaurant_menu_list";
    }

    // ➕ Show form to add a new menu item
    @GetMapping("/add")
    public String showAddForm(@PathVariable Long restaurantId, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return "redirect:/admin/restaurants?error=notfound";
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);

        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("menuItem", menuItem);
        return "admin/add_menu_item";
    }

    // ✅ Handle submission of new menu item
    @PostMapping("/add")
    public String addMenuItem(@PathVariable Long restaurantId,
                              @ModelAttribute MenuItem menuItem,
                              RedirectAttributes redirectAttributes) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            redirectAttributes.addFlashAttribute("error", "Restaurant not found.");
            return "redirect:/admin/restaurants";
        }

        menuItem.setRestaurant(restaurant);
        menuItemService.saveMenuItem(menuItem);
        redirectAttributes.addFlashAttribute("message", "Dish \"" + menuItem.getName() + "\" added successfully.");
        return "redirect:/admin/restaurants/" + restaurantId + "/menu";
    }

    // 🔄 Toggle stock status of a menu item
    @PostMapping("/{itemId}/toggle-stock")
    public String toggleStock(@PathVariable Long restaurantId,
                              @PathVariable Long itemId,
                              RedirectAttributes redirectAttributes) {
        MenuItem item = menuItemService.findById(itemId);
        if (item != null && item.getRestaurant().getId().equals(restaurantId)) {
            item.setInStock(!item.isInStock());
            menuItemService.saveMenuItem(item);
            redirectAttributes.addFlashAttribute("message",
                "Item \"" + item.getName() + "\" stock status updated.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Menu item not found or mismatched.");
        }
        return "redirect:/admin/restaurants/" + restaurantId + "/menu";
    }

    // ✏️ Show form to edit a menu item
    @GetMapping("/edit/{itemId}")
    public String showEditForm(@PathVariable Long restaurantId,
                               @PathVariable Long itemId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        MenuItem menuItem = menuItemService.findById(itemId);
        if (menuItem == null || !menuItem.getRestaurant().getId().equals(restaurantId)) {
            redirectAttributes.addFlashAttribute("error", "Menu item not found.");
            return "redirect:/admin/restaurants/" + restaurantId + "/menu";
        }

        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("menuItem", menuItem);
        return "admin/edit_menu_item";
    }

    // ✅ Handle submission of edited menu item
    @PostMapping("/edit/{itemId}")
    public String updateMenuItem(@PathVariable Long restaurantId,
                                 @PathVariable Long itemId,
                                 @ModelAttribute MenuItem updatedItem,
                                 RedirectAttributes redirectAttributes) {
        MenuItem existingItem = menuItemService.findById(itemId);
        if (existingItem == null || !existingItem.getRestaurant().getId().equals(restaurantId)) {
            redirectAttributes.addFlashAttribute("error", "Menu item not found.");
            return "redirect:/admin/restaurants/" + restaurantId + "/menu";
        }

        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setInStock(updatedItem.isInStock());

        menuItemService.saveMenuItem(existingItem);
        redirectAttributes.addFlashAttribute("message", "Item \"" + existingItem.getName() + "\" updated.");
        return "redirect:/admin/restaurants/" + restaurantId + "/menu";
    }

    // 🗑️ Delete a menu item
    @GetMapping("/delete/{itemId}")
    public String deleteMenuItem(@PathVariable Long restaurantId,
                                 @PathVariable Long itemId,
                                 RedirectAttributes redirectAttributes) {
        MenuItem item = menuItemService.findById(itemId);
        if (item != null && item.getRestaurant().getId().equals(restaurantId)) {
            menuItemService.deleteMenuItem(itemId);
            redirectAttributes.addFlashAttribute("message", "Item \"" + item.getName() + "\" deleted.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Menu item not found.");
        }
        return "redirect:/admin/restaurants/" + restaurantId + "/menu";
    }
}