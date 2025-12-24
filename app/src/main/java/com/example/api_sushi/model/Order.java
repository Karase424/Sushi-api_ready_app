package com.example.api_sushi.model;

import java.util.Date;
import java.util.List;
import com.example.api_sushi.model.CartItem;

public class Order {
    private String orderId;
    private Date date;
    private List<CartItem> items;
    private double totalAmount;
    private String status;
    private String userId;

    public Order() {}

    public Order(String orderId, Date date, List<CartItem> items, double totalAmount, String status, String userId) {
        this.orderId = orderId;
        this.date = date;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
