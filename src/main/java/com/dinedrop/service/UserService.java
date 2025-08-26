package com.dinedrop.service;

import com.dinedrop.model.User;
import com.dinedrop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ Authenticate user
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Found user: " + user.getEmail() + ", role: " + user.getRole());
            return user;
        }
        return null;
    }

    // ✅ Invalidate session
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // ✅ Save new user
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // ✅ Get user from session
    public User getLoggedInUser(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        return (obj instanceof User) ? (User) obj : null;
    }

    // ✅ Find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
