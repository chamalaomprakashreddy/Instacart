package com.example.ecomerce;

public class ShoppingProduct {
        private String name;
        private double price;

        public ShoppingProduct(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }
        public double getPrice() {
            return price;
        }
    }


