package com.dinedrop.controller;

import com.dinedrop.model.User;
import com.dinedrop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            return "redirect:/login";
        }

        User user = userService.findByEmail(formUser.getEmail());
        if (user != null && passwordEncoder.matches(formUser.getPassword(), user.getPassword())) {
            session.setAttribute("loggedInUser", user);

            if (user.getRole() != null && user.getRole().contains("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/home";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "redirect:/login";
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

        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            model.addAttribute("error", "User already exists");
            return "register";
        }

        user.setRole("USER"); // Default role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        model.addAttribute("message", "Registration successful. Please login.");
        return "redirect:/login";
    }

    // Forgot password form
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot_password";
    }

    // Process forgot password (session-based)
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model, HttpSession session) {
        User user = userService.findByEmail(email);
        if (user != null) {
            session.setAttribute("resetUserId", user.getId());
            return "redirect:/reset-password";
        } else {
            model.addAttribute("error", "User not found");
            return "forgot_password";
        }
    }

    // Show reset password form safely
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("resetUserId");
        if (userId != null) {
            model.addAttribute("userId", userId);
            return "reset_password";
        } else {
            model.addAttribute("error", "Invalid reset attempt. Please try again.");
            return "forgot_password";
        }
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                Model model) {
        Long userId = (Long) session.getAttribute("resetUserId");
        if (userId == null) {
            model.addAttribute("error", "Session expired. Please try again.");
            return "forgot_password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("userId", userId);
            return "reset_password";
        }

        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);
            session.removeAttribute("resetUserId");
            model.addAttribute("message", "Password reset successful. Please login.");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "User not found");
            return "forgot_password";
        }
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
        model.addAttribute("user", user);
        return "admin/admin_dashboard";
    }
}