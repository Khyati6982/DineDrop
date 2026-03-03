package com.dinedrop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SuccessController {

    @GetMapping("/success")
    public String successPage(@RequestParam(name = "session_id", required = false) String sessionId,
                              Model model) {
        // Pass the Stripe session_id to the template
        model.addAttribute("sessionId", sessionId);
        return "success"; 
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "cancel";
    }
}