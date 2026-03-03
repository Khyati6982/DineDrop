package com.dinedrop.controller;

import com.dinedrop.model.Order;
import com.dinedrop.service.CartService;
import com.dinedrop.service.OrderServiceImpl;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private CartService cartService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        // Read raw payload from Stripe
        String payload = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        logger.info("Webhook payload received: {}", payload);

        Event event;
        try {
            // Verify Stripe signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            logger.info("Webhook verified: {}", event.getType());
        } catch (Exception e) {
            logger.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Webhook signature verification failed.");
        }

        // Handle successful checkout session
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null && session.getClientReferenceId() != null) {
                logger.info("Checkout session completed for orderId={}", session.getClientReferenceId());
                try {
                    Long orderId = Long.valueOf(session.getClientReferenceId());

                    // Update order status in DB
                    orderService.updatePaymentStatus(orderId, "PAID", session.getId());

                    // Clear cart only after payment success
                    Order order = orderService.getOrderDetails(orderId);
                    cartService.clearCart(order.getUser());

                } catch (NumberFormatException e) {
                    logger.error("Invalid orderId in clientReferenceId: {}", session.getClientReferenceId());
                }
            } else {
                logger.warn("Checkout session missing clientReferenceId or session object");
            }
        }

        return ResponseEntity.ok("Webhook received");
    }
}