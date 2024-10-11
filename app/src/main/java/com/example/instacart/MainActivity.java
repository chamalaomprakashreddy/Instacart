package com.example.instacart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private Map<Integer, Class<?>> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        // Set click listeners for login and create buttons
        findViewById(R.id.loginBTN).setOnClickListener(view -> navigateTo(LoginActivity.class));
        findViewById(R.id.createBTN).setOnClickListener(view -> navigateTo(CreateAccountActivity.class));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initializeActivityMap(); // Initialize activity map

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> destinationActivity = activityMap.get(itemId); // Get the destination activity from the map

            if (destinationActivity != null) {
                startActivity(new Intent(MainActivity.this, destinationActivity));
                return true;
            }

            return false;
        });
    }

    private void initializeActivityMap() {
        activityMap = new HashMap<>();
        activityMap.put(R.id.navigation_home, ProductListActivity.class);
        activityMap.put(R.id.navigation_cart, ShoppingCartActivity.class);
        activityMap.put(R.id.navigation_profile, UserProfileActivity.class); // Profile activity
    }

    private void navigateTo(Class<?> destination) {
        startActivity(new Intent(MainActivity.this, destination));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, hide the bottom navigation menu
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            // If user is logged in, show the bottom navigation menu
            bottomNavigationView.setVisibility(View.VISIBLE);
            // Navigate to ProductListActivity and finish MainActivity
            navigateTo(ProductListActivity.class);
            finish();
        }
    }
}



