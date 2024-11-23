package com.example.ecomerce;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import java.util.Map;
import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameEditText, nameEditText, emailEditText, phoneNumberEditText, passwordEditText, addressEditText;

    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.editTextUsername);
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        passwordEditText = findViewById(R.id.editTextPassword);
        addressEditText = findViewById(R.id.editTextAddress);
        createAccountButton = findViewById(R.id.buttonCreateAccount);
        TextView loginLink = findViewById(R.id.textViewLoginLink);
        loginLink.setOnClickListener(view -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("name", name); // Use "name" key for the user's name
                user.put("email", email);
                user.put("phoneNumber", phoneNumber);
                user.put("address", address);
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                // Redirect to another activity or show a success message
                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                // Handle errors
                            }
                        });

                if (!username.isEmpty() && !name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty()) {
                    registerUser(username,name, email, phoneNumber, password,address);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String username,String name, String email, String phoneNumber, String password, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success
                            Toast.makeText(CreateAccountActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            // Save additional user information in Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("username", username);
                                userData.put("name", name);
                                userData.put("email", email);
                                userData.put("phoneNumber", phoneNumber);
                                userData.put("address", address);

                                // Add a new document with a generated ID
                                db.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Document added successfully
                                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to add document
                                                Toast.makeText(CreateAccountActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Registration failed
                            Toast.makeText(CreateAccountActivity.this, "Registration failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
