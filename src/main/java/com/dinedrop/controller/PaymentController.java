package com.dinedrop.controller;

import com.dinedrop.dto.OrderRequest;
import com.dinedrop.dto.OrderItemRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

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

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://your-frontend.com/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://your-frontend.com/cancel")
            .addAllLineItem(lineItems)
            .setClientReferenceId(String.valueOf(orderRequest.getOrderId()))
            .build();

        Session session = Session.create(params);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("url", session.getUrl());

        return ResponseEntity.ok(responseData);
    }
}