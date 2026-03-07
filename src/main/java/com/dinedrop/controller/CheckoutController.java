package com.dinedrop.controller;

import com.dinedrop.model.CartItem;
import com.dinedrop.model.Order;
import com.dinedrop.model.User;
import com.dinedrop.service.CartService;
import com.dinedrop.service.OrderServiceImpl;
import com.dinedrop.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private CartService cartService;

    @Value("${app.baseUrl}")
    private String baseUrl;

    // Show checkout page
    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model) {
        User user = userService.getLoggedInUser(session);
        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        double totalAmount = cartService.getTotalPrice(user);

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "checkout";
    }

    // Place order and redirect to Stripe Checkout
    @PostMapping("/place")
    public String placeOrder(
            @RequestParam List<Long> menuItemIds,
            @RequestParam List<Integer> quantities,
            @RequestParam String deliveryAddress,
            HttpSession session,
            Model model) throws StripeException {

        User user = userService.getLoggedInUser(session);
        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        // Place order in DB (status = PENDING)
        Order order = orderService.placeOrder(user, menuItemIds, quantities, deliveryAddress);

        // Build Stripe Checkout Session
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (int i = 0; i < menuItemIds.size(); i++) {
            Long menuItemId = menuItemIds.get(i);
            int quantity = quantities.get(i);

            CartItem cartItem = cartService.getCartItemByMenuItemId(user, menuItemId);
            String itemName = cartItem.getMenuItem().getName();
            double itemPrice = cartItem.getMenuItem().getPrice();

            lineItems.add(
                SessionCreateParams.LineItem.builder()
                    .setQuantity((long) quantity)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("inr")
                            .setUnitAmount((long) (itemPrice * 100))
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(itemName)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            );
        }

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/cancel")
            .addAllLineItem(lineItems)
            .setClientReferenceId(String.valueOf(order.getId()))
            .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
            .setCustomerEmail(user.getEmail())
            .build();

        Session sessionStripe = Session.create(params);

        // Redirect user to Stripe Checkout
        return "redirect:" + sessionStripe.getUrl();
    }
}