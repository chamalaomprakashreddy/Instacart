package com.example.ecomerce;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    private EditText emailEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    public void resetPassword(View view) {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assume you have a Firestore collection called "users"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        // Query the users collection for the provided email
        Query query = usersRef.whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Email exists in the users collection
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Email does not exist in the users collection
                    Toast.makeText(this, "User with provided email does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Error querying the users collection
                Toast.makeText(this, "Error checking email existence", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error checking email existence", task.getException());
            }
        });
    }

}
