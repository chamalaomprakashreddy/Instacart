package com.example.ecomerce;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productsRef = db.collection("products");
    private List<Product> productList = new ArrayList<>();
    private List<Product> cartItemList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private Map<Integer, Class<?>> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(productList, this, recyclerView);
        recyclerView.setAdapter(adapter);

        retrieveProducts();

        adapter.setOnAddItemClickListener(new ProductAdapter.OnAddItemClickListener() {
            @Override
            public void onAddItemClick(int position) {
                Product clickedProduct = productList.get(position);
                addToCart(clickedProduct);
                adapter.updateButtonVisibility(position, true); // Show buttons after adding to cart
            }
        });

        Button myCartButton = findViewById(R.id.myCartBTN);
        myCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, ShoppingCartActivity.class);
                intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItemList));
                startActivity(intent);
            }
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initializeActivityMap(); // Uncomment to initialize activity map

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> destinationActivity = activityMap.get(itemId); // Get the destination activity from the map

            if (destinationActivity != null) {
                startActivity(new Intent(ProductListActivity.this, destinationActivity));
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
private void retrieveProducts() {
    productsRef.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            productList.clear(); // Clear the existing product list
            for (QueryDocumentSnapshot document : task.getResult()) {
                // Convert each document into a Product object
                Product product = document.toObject(Product.class);
                productList.add(product); // Add the product to the list
            }
            adapter.notifyDataSetChanged(); // Notify the adapter about data change
        } else {
            Log.e("ProductListActivity", "Error getting products", task.getException());
        }
    });
}



    private void addToCart(Product product) {
        cartItemList.add(product);

        // Log the added product
        Log.d("ProductListActivity", "Added product to cart: " + product.getName());

        // Log the updated cartItemList
        Log.d("ProductListActivity", "Cart items after adding: " + cartItemList.toString());
    }
}

