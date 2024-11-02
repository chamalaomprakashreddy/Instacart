package com.example.instacart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity {

    private List<Product> cartItemList;
    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private TextView totalTV;
    private BottomNavigationView bottomNavigationView;
    private Map<Integer, Class<?>> activityMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve cart items from the intent
        cartItemList = getIntent().getParcelableArrayListExtra("cartItems");

        // If cartItemList is null, initialize an empty list
        if (cartItemList == null) {
            cartItemList = new ArrayList<>();
        }

        // Initialize adapter with cart items
        adapter = new ShoppingAdapter(cartItemList, this);
        recyclerView.setAdapter(adapter);

        // Initialize the ImageButton
        ImageButton backButton = findViewById(R.id.imageView);

        // Set click listener for the ImageButton to navigate to ProductListActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click, navigate to the ProductListActivity
                Intent productListIntent = new Intent(ShoppingCartActivity.this, ProductListActivity.class);
                startActivity(productListIntent);
            }
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initializeActivityMap(); // Initialize activity map
        int selectedItem = -1;
        if (getClass().getSimpleName().equals("ProductListActivity")) {
            selectedItem = R.id.navigation_home;
        } else if (getClass().getSimpleName().equals("ShoppingCartActivity")) {
            selectedItem = R.id.navigation_cart;
        } else if (getClass().getSimpleName().equals("UserProfileActivity")) {
            selectedItem = R.id.navigation_profile;
        }

// Set the selected item in the Bottom Navigation View
        if (selectedItem != -1) {
            bottomNavigationView.setSelectedItemId(selectedItem);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> destinationActivity = activityMap.get(itemId); // Get the destination activity from the map

            if (destinationActivity != null) {
                startActivity(new Intent(ShoppingCartActivity.this, destinationActivity));
                return true;
            }

            return false;
        });

        // Set click listener for the Pay Now button
        Button payNowButton = findViewById(R.id.checkoutBTN);
        payNowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (cartItemList.isEmpty()) {
                    Toast.makeText(ShoppingCartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    ArrayList<OrderItem> orderItems = new ArrayList<>();
                    for (Product product : cartItemList) {
                        OrderItem orderItem = new OrderItem(product.getName(), product.getPrice(), 1, product.getImageUrl());
                        orderItems.add(orderItem);
                    }

                    // Handle the click, navigate to the CheckoutActivity
                    Intent checkoutIntent = new Intent(ShoppingCartActivity.this, CheckoutActivity.class);
                    checkoutIntent.putParcelableArrayListExtra("cartItems", (ArrayList<? extends Parcelable>) orderItems);

                    startActivity(checkoutIntent);
                }

            }
        });

        if (cartItemList.isEmpty()) {
            payNowButton.setEnabled(false);
        } else {
            payNowButton.setEnabled(true);
        }
        // Initialize the TextView for displaying total price
        totalTV = findViewById(R.id.totalTV);

        // Calculate and display total price
        double totalPrice = calculateTotalPrice(cartItemList);
        totalTV.setText("Total: $" + totalPrice);
    }
    private void initializeActivityMap() {
        activityMap = new HashMap<>();
        activityMap.put(R.id.navigation_home, ProductListActivity.class);
        activityMap.put(R.id.navigation_cart, ShoppingCartActivity.class);
        activityMap.put(R.id.navigation_profile, UserProfileActivity.class); // Profile activity
    }
    // Method to calculate total price of all products in the cart
    private double calculateTotalPrice(List<Product> products) {
        double totalPrice = 0;
        for (Product product : products) {
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }

}
