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
@RequestMapping("/admin/menu")
public class AdminMenuController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private RestaurantService restaurantService;

    // ✅ Show all menu items (global view)
    @GetMapping
    public String listMenuItems(Model model) {
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        return "admin/menu_list";
    }

    // ✅ Show add form (global, not restaurant-scoped)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/add_menu_item";
    }

    // ✅ Handle add form submission
    @PostMapping("/add")
    public String addMenuItem(@ModelAttribute MenuItem menuItem,
                              RedirectAttributes redirectAttributes) {
        Restaurant restaurant = menuItem.getRestaurant();
        if (restaurant == null || restaurant.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Restaurant must be selected.");
            return "redirect:/admin/menu/add";
        }

        menuItemService.saveMenuItem(menuItem);
        return "redirect:/admin/restaurants/" + restaurant.getId() + "/menu";
    }

    // ✅ Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MenuItem item = menuItemService.findById(id);
        if (item == null) {
            return "redirect:/admin/menu?error=notfound";
        }

        model.addAttribute("menuItem", item);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());

        // ✅ Add restaurantId explicitly
        if (item.getRestaurant() != null) {
            model.addAttribute("restaurantId", item.getRestaurant().getId());
        }

        return "admin/edit_menu_item";
    }

    // ✅ Handle edit form submission
    @PostMapping("/edit/{id}")
    public String updateMenuItem(@PathVariable Long id,
                                 @ModelAttribute MenuItem menuItem,
                                 RedirectAttributes redirectAttributes) {
        MenuItem existing = menuItemService.findById(id);
        if (existing != null) {
            existing.setName(menuItem.getName());
            existing.setDescription(menuItem.getDescription());
            existing.setPrice(menuItem.getPrice());
            existing.setRestaurant(menuItem.getRestaurant());
            existing.setInStock(menuItem.isInStock());
            menuItemService.saveMenuItem(existing);
        }

        Long restaurantId = menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null;
        if (restaurantId != null) {
            return "redirect:/admin/restaurants/" + restaurantId + "/menu";
        }
        return "redirect:/admin/menu";
    }

    // ✅ Toggle stock status
    @PostMapping("/{itemId}/toggle-stock")
    public String toggleStock(@PathVariable Long itemId,
                              RedirectAttributes redirectAttributes) {
        MenuItem item = menuItemService.findById(itemId);
        if (item != null) {
            item.setInStock(!item.isInStock());
            menuItemService.saveMenuItem(item);
            redirectAttributes.addFlashAttribute("message",
                "Item \"" + item.getName() + "\" stock status updated.");

            Long restaurantId = item.getRestaurant() != null ? item.getRestaurant().getId() : null;
            if (restaurantId != null) {
                return "redirect:/admin/menu";
            }
        }
        return "redirect:/admin/menu";
    }

    // ✅ Delete menu item
    @GetMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        MenuItem item = menuItemService.findById(id);
        Long restaurantId = (item != null && item.getRestaurant() != null) ? item.getRestaurant().getId() : null;

        menuItemService.deleteMenuItem(id);

        if (restaurantId != null) {
            return "redirect:/admin/restaurants/" + restaurantId + "/menu";
        }
        return "redirect:/admin/menu";
    }
}
