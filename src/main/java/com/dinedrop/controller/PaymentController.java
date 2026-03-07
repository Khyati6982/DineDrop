package com.dinedrop.controller;

import com.dinedrop.dto.OrderRequest;
import com.dinedrop.dto.OrderItemRequest;
import com.dinedrop.model.Order;
import com.dinedrop.service.OrderServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderServiceImpl orderService;

    @Value("${app.baseUrl}")
    private String baseUrl; 

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody OrderRequest orderRequest) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (OrderItemRequest item : orderRequest.getItems()) {
            lineItems.add(
                SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("inr")
                            .setUnitAmount((long) (item.getPrice() * 100)) 
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getName())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            );
        }

        // Build Checkout Session with dynamic baseUrl
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/cancel")
            .addAllLineItem(lineItems)
            .setClientReferenceId(String.valueOf(orderRequest.getOrderId())) // link orderId
            .build();

        Session session = Session.create(params);

        // Save Stripe session ID immediately in DB
        Order order = orderService.getOrderDetails(orderRequest.getOrderId());
        order.setStripeSessionId(session.getId());
        order.setPaymentStatus("PENDING");
        orderService.save(order);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("url", session.getUrl());
        responseData.put("sessionId", session.getId());

        return ResponseEntity.ok(responseData);
    }
}