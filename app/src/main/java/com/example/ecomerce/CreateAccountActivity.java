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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

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

                if (!validatePassword(password)) {
                    Toast.makeText(CreateAccountActivity.this,
                            "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!username.isEmpty() && !name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty() && !address.isEmpty()) {
                    registerUser(username, name, email, phoneNumber, password, address);

                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("name", name);
                    user.put("email", email);
                    user.put("phoneNumber", phoneNumber);
                    user.put("address", address);

                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error adding document", e);
                            });
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    

