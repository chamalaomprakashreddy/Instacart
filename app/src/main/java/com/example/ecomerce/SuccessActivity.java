package com.example.ecomerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        String orderID = intent.getStringExtra("orderID");

        double grandTotal = intent.getDoubleExtra("grandTotal", 0.0); // Default value 0.0 if not found
        String formattedGrandTotal = String.format("%.2f", grandTotal);
        // Display order ID and grand total in TextViews

        TextView orderIdTextView = findViewById(R.id.textViewOrderID);
        orderIdTextView.setText("Order ID: #" + orderID);

        TextView grandTotalTextView = findViewById(R.id.textViewGrandTotal);
        grandTotalTextView.setText("Grand Total: $" + formattedGrandTotal);
        Button payNowButton = findViewById(R.id.buttonContinueShopping);

        // Set click listener for the Pay Now button
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click, navigate to the CheckpageActivity
                Intent checkpageIntent = new Intent(SuccessActivity.this, ProductListActivity.class);
                startActivity(checkpageIntent);
            }
        });
    }
}