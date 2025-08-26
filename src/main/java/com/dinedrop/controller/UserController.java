package com.dinedrop.controller;

import com.dinedrop.model.User;
import com.dinedrop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Show login page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // Handle login form
    @PostMapping("/login")
    public String login(@ModelAttribute("user") User formUser, HttpSession session, Model model) {
        if (formUser.getEmail() == null || formUser.getPassword() == null) {
            model.addAttribute("error", "Email and password are required.");
            return "login";
        }

        User user = userService.login(formUser.getEmail(), formUser.getPassword());
        if (user != null) {
            System.out.println("Logged in user role: " + user.getRole());
            session.setAttribute("loggedInUser", user);

            if (user.getRole() != null && user.getRole().contains("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/home";
            }
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // Show registration page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle registration form
    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (user.getEmail() == null || user.getPassword() == null) {
            model.addAttribute("error", "Email and password are required.");
            return "register";
        }

        User existing = userService.findByEmail(user.getEmail()); // ✅ safer than login()
        if (existing != null) {
            model.addAttribute("error", "User already exists");
            return "register";
        }

        user.setRole("USER"); // Default role
        userService.saveUser(user);
        model.addAttribute("message", "Registration successful. Please login.");
        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        userService.logout(session);
        return "redirect:/login";
    }

    // User home
    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null || user.getRole() == null || !user.getRole().contains("USER")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user_home";
    }

    // Admin dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null || user.getRole() == null || !user.getRole().contains("ADMIN")) {
            return "redirect:/login";
        }
        System.out.println("Admin username: " + user.getUsername());
        model.addAttribute("user", user);
        return "admin_dashboard";
    }
}
