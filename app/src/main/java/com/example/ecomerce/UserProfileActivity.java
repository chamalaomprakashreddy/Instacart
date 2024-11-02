package com.example.ecomerce;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private EditText editNameET;
    private EditText editEmailET;
    private EditText editPhoneET;
    private EditText editPasswordET;
    private EditText editAddressET;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean isEditing = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Button saveChangesButton = findViewById(R.id.saveChangesBTN);
        saveChangesButton.setVisibility(View.GONE);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is authenticated, proceed with setting up the activity
            nameTextView = findViewById(R.id.nameTV);
            emailTextView = findViewById(R.id.emailTV);
            editNameET = findViewById(R.id.editNameET);
            editEmailET = findViewById(R.id.editEmailET);
            editPhoneET = findViewById(R.id.editPhoneET);
//            editPasswordET = findViewById(R.id.editPasswordET);
            editAddressET = findViewById(R.id.editAddressET);

            Button editButton = findViewById(R.id.editBTN);

            Button logoutButton = findViewById(R.id.logOutBTN);

            saveChangesButton.setVisibility(View.GONE);
            saveChangesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Implement save changes functionality here
                    saveChangesButton.setVisibility(View.VISIBLE);
                    saveChanges();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleEditVisibility(true);
                }
            });
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement save changes functionality here
                saveChanges();
            }
        });
                    db.collection("users")
                .whereEqualTo("email", currentUser.getEmail()) // Assuming email is unique
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                // Display user's name in TextView
                                nameTextView.setText(name);
                            }

                        } else {
                            Log.d(TAG, "Error getting user details: ", task.getException());
                        }
                    }
                });
        getUserDetails();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                finish();
            }
        });


        } else {
            // User is not authenticated, redirect to login screen
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void getUserDetails() {
        db.collection("users")
                .whereEqualTo("email", currentUser.getEmail()) // Assuming email is unique
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("username");
                                String email = document.getString("email");
                                String address = document.getString("address");
                                String phoneNumber = document.getString("phoneNumber");
                                // Display user's name and email in TextViews
                                nameTextView.setText(name);
                                editNameET.setText(name);
                                emailTextView.setText(email);
                                editEmailET.setText(email);
                                editPhoneET.setText(phoneNumber);
                                editAddressET.setText(address);
                            }
                        } else {
                            Log.d(TAG, "Error getting user details: ", task.getException());
                        }
                    }
                });
    }

    private void toggleEditVisibility(boolean isEditing) {
        this.isEditing = isEditing;
        Button saveChangesButton = findViewById(R.id.saveChangesBTN);
        if (isEditing) {
            nameTextView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            editNameET.setVisibility(View.VISIBLE);
            editEmailET.setVisibility(View.VISIBLE);
            editPhoneET.setVisibility(View.VISIBLE);
            editAddressET.setVisibility(View.VISIBLE);
            saveChangesButton.setVisibility(View.VISIBLE);

        } else {
            nameTextView.setVisibility(View.VISIBLE);
            emailTextView.setVisibility(View.VISIBLE);

            editNameET.setVisibility(View.GONE);
            editEmailET.setVisibility(View.GONE);
            editPhoneET.setVisibility(View.GONE);
//            editPasswordET.setVisibility(View.GONE);
            editAddressET.setVisibility(View.GONE);
            saveChangesButton.setVisibility(View.GONE);
            // Update TextViews with edited values
            nameTextView.setText(editNameET.getText().toString());
            emailTextView.setText(editEmailET.getText().toString());
        }
    }

    private void saveChanges() {
        String newName = editNameET.getText().toString().trim();
        String newEmail = editEmailET.getText().toString().trim();
        String newPhone = editPhoneET.getText().toString().trim();
        String newAddress = editAddressET.getText().toString().trim();

        // Update user details in Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .update("username", newName,
                        "email", newEmail,
                        "phoneNumber", newPhone,
                        "address", newAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserProfileActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                            toggleEditVisibility(false);
                        } else {
                            Log.e(TAG, "Error updating user details: ", task.getException());
                            Toast.makeText(UserProfileActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
