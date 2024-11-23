package com.example.ecomerce;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCartItems;
    private OrderAdapter orderAdapter;
    private List<OrderItem> cartItems;
    private TextView totalPriceTextView;
    private CollectionReference ordersRef; // Changed to CollectionReference
    private String deliveryAddress;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ordersRef = FirebaseFirestore.getInstance().collection("orders"); // Changed to FirebaseFirestore
        Button payNowButton = findViewById(R.id.buttonPay);
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        totalPriceTextView = findViewById(R.id.deliveryValue);
//        Intent intent = getIntent();
//        ArrayList<OrderItem> cartItems = (ArrayList<OrderItem>) intent.getSerializableExtra("cartItems");
        Intent intent = getIntent();
        ArrayList<OrderItem> cartItems = (ArrayList<OrderItem>) intent.getSerializableExtra("cartItems");

        deliveryAddress = "123 Main St, City, Country"; // Example delivery address, replace it with your actual logic to get the delivery address
        if (intent != null && intent.hasExtra("cartItems")) {
            cartItems = (ArrayList<OrderItem>) intent.getSerializableExtra("cartItems");
            if (cartItems != null) {
                for (OrderItem item : cartItems) {
                    Log.d("CheckoutActivity", "Shopping Cart Item: " + item.getName());
                    Log.d("CheckoutActivity", "Shopping Cart Item Price: " + item.getPrice());
                }
            }
        } else {
            cartItems = new ArrayList<>();
            Log.d("CheckoutActivity","checkout page not have any items to checkout");
        }
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            Log.d("CheckoutActivity", "checkout page does not have any items to checkout");
        }
        orderAdapter = new OrderAdapter(cartItems);
        recyclerViewCartItems.setAdapter(orderAdapter);
        // Calculate total price
        double totalPrice = calculateTotalPrice(cartItems);
        double deliveryCharge = totalPrice * 0.02; // 2% delivery charge
        double grandTotal = totalPrice + deliveryCharge;

        // Display total price including delivery charge and grand total
        totalPriceTextView.setText(String.format("Total: $%.2f\nDelivery Charge: $%.2f\nGrand Total: $%.2f", totalPrice, deliveryCharge, grandTotal));

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                ArrayList<OrderItem> cartItems = (ArrayList<OrderItem>) intent.getSerializableExtra("cartItems");
                String orderID = generateOrderID();
                Order order = createOrder(cartItems);
                if (order != null) {
                    saveOrderToFirebase(order);
                    // Handle the click, navigate to the SuccessActivity
                    Intent successIntent = new Intent(CheckoutActivity.this, SuccessActivity.class);
                    successIntent.putExtra("orderID", orderID);
                    successIntent.putExtra("grandTotal", grandTotal);
                    startActivity(successIntent);
                } else {
                    // Optionally, display a message to the user indicating that order creation failed
                    Toast.makeText(CheckoutActivity.this, "Failed to create order. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            private void saveOrderToFirebase(Order order) {
                if (order != null) {
                    String orderId = order.getOrderId();
                    if (orderId != null) {
                        ordersRef.document(orderId).set(order);
                    } else {
                        Log.e("CheckoutActivity", "Order ID is null.");
                    }
                } else {
                    Log.e("CheckoutActivity", "Order object is null.");
                }
            }
        });
    }
//    private Order createOrder() {
//        String orderID = generateOrderID();
//
//
//        if (cartItems != null) {
//            double totalPrice = calculateTotalPrice(cartItems);
//            double deliveryCharge = totalPrice * 0.02;
//            double grandTotal = totalPrice + deliveryCharge;
//            return new Order(orderID, cartItems, totalPrice, deliveryCharge, grandTotal, deliveryAddress);
//        }
//        return null;
//    }
private Order createOrder(ArrayList<OrderItem> cartItems) {
    String orderID = generateOrderID();
    if (cartItems != null && !cartItems.isEmpty()) {
        double totalPrice = calculateTotalPrice(cartItems);
        double deliveryCharge = totalPrice * 0.02;
        double grandTotal = totalPrice + deliveryCharge;
        return new Order(orderID, cartItems, totalPrice, deliveryCharge, grandTotal, deliveryAddress);
    } else {
        Log.e("CheckoutActivity", "Cart items list is null or empty.");
        // Optionally, handle this case by displaying an error message to the user
        return null; // Return null to indicate that order creation failed
    }
}


    private String generateOrderID() {
        // Generate a random UUID as the order ID
        return UUID.randomUUID().toString();
    }

//    private void saveOrderToFirebase(Order order) {
//        // Push the order object to Firebase Firestore
//        ordersRef.document(order.getOrderId()).set(order);
//    }
private void saveOrderToFirebase(Order order) {
    if (order != null) {
        db = FirebaseFirestore.getInstance();
        ordersRef = db.collection("orders");

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderID", order.getOrderId());
        orderData.put("items", order.getItems()); // You may need to convert items to a serializable format if necessary
        orderData.put("cost", order.getCost());
        orderData.put("deliveryCharge", order.getDeliveryCharge());
        orderData.put("grandTotal", order.getGrandTotal());
        orderData.put("deliveryAddress", order.getDeliveryAddress());

        ordersRef.document(order.getOrderId()).set(orderData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Order successfully saved to Firestore");
                        // Handle success if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error saving order to Firestore", e);
                        // Handle failure if needed
                    }
                });
    } else {
        Log.e(TAG, "Order object is null. Cannot save to Firestore.");
    }
}
    private double calculateTotalPrice(List<OrderItem> items) {
        double totalPrice = 0;
        if (items != null) {
            for (OrderItem item : items) {
                totalPrice += item.getPrice();
            }
        }
        return totalPrice;
    }

}
