package com.example.instacart;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private boolean addedToCart;
    private int quantity; // New field for quantity

    public Product() {
        // Default constructor required for Firestore
    }

    // Constructors
    public Product(String name, String description, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.addedToCart = false;
        this.quantity = 0; // Initialize quantity to 0
    }

    // Getters and setters for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Other getters and setters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isAddedToCart() {
        return addedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        this.addedToCart = addedToCart;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        addedToCart = in.readByte() != 0;
        quantity = in.readInt(); // Read quantity from Parcel
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (addedToCart ? 1 : 0));
        dest.writeInt(quantity); // Write quantity to Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

