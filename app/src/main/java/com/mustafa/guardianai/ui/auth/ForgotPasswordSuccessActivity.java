package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafa.guardianai.databinding.ActivityForgotPasswordSuccessBinding;

/**
 * Forgot Password Success Activity
 * Shows success message after password reset email is sent
 */
public class ForgotPasswordSuccessActivity extends AppCompatActivity {
    private ActivityForgotPasswordSuccessBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String emailAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get email from intent
        emailAddress = getIntent().getStringExtra("email");
        if (emailAddress == null || emailAddress.isEmpty()) {
            emailAddress = "your email";
        }

        setupUI();
    }

    private void setupUI() {
        // Set email address
        binding.tvEmailAddress.setText(emailAddress);

        // Back to Login button
        binding.btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Resend link
        binding.tvResend.setOnClickListener(v -> {
            if (emailAddress != null && !emailAddress.isEmpty() && !emailAddress.equals("your email")) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnBackToLogin.setEnabled(false);
                
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnSuccessListener(aVoid -> {
                            runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.btnBackToLogin.setEnabled(true);
                                Toast.makeText(ForgotPasswordSuccessActivity.this,
                                        "Password reset email resent! Please check your inbox.",
                                        Toast.LENGTH_LONG).show();
                            });
                        })
                        .addOnFailureListener(e -> {
                            runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.btnBackToLogin.setEnabled(true);
                                Toast.makeText(ForgotPasswordSuccessActivity.this,
                                        "Failed to resend: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                        });
            } else {
                Toast.makeText(this, "Email address not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


