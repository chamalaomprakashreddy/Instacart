package com.example.instacart;

import java.util.List;

public class Order {
    private String orderID;
    private List<OrderItem> items;
    private double cost;
    private double deliveryCharge;
    private double grandTotal;
    private String deliveryAddress;

    public Order(String orderID, List<OrderItem> items, double cost, double deliveryCharge, double grandTotal, String deliveryAddress) {
        this.orderID = orderID;
        this.items = items;
        this.cost = cost;
        this.deliveryCharge = deliveryCharge;
        this.grandTotal = grandTotal;
        this.deliveryAddress = deliveryAddress;
    }
    public String getOrderId() {
        return orderID;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public double getCost() {
        return cost;
    }
    public double getDeliveryCharge() {
        return deliveryCharge;
    }
    public double getGrandTotal() {
        return grandTotal;
    }
    public String getDeliveryAddress() {
        return deliveryAddress;
    }}
// Getters and setters
