package com.dinedrop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalAmount;

    private String deliveryAddress;

    private String paymentStatus; // e.g., "PAID", "PENDING", "FAILED"

    private String stripeSessionId; 
    
    private LocalDateTime orderTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // Constructors
    public Order() {
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }
    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
