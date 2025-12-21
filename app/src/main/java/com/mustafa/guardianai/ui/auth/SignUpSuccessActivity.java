package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafa.guardianai.databinding.ActivitySignupSuccessBinding;

/**
 * Sign Up Success Activity
 * Shows success message after account creation
 */
public class SignUpSuccessActivity extends AppCompatActivity {
    private ActivitySignupSuccessBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        // Set email address if available
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            binding.tvEmailAddress.setText(user.getEmail());
        }

        // Continue button - navigate to login
        // User should verify email via link, then login
        binding.btnContinue.setOnClickListener(v -> {
            // Sign out the user so they can login after verifying email
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Login link
        binding.tvLogin.setOnClickListener(v -> {
            // Sign out the user so they can login
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}


