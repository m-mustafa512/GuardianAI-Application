package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafa.guardianai.databinding.ActivityForgotPasswordBinding;
import com.mustafa.guardianai.utils.EmailValidator;
import com.mustafa.guardianai.utils.PasswordValidator;

/**
 * Forgot Password Activity
 * Handles password reset via email
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Send Reset Link button
        binding.btnSendResetLink.setOnClickListener(v -> handlePasswordReset());

        // Login link
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handlePasswordReset() {
        String email = binding.etEmail.getText().toString().trim();

        // Validate email
        PasswordValidator.ValidationResult emailValidation = EmailValidator.validate(email);
        if (!emailValidation.isValid()) {
            binding.tilEmail.setError(emailValidation.getErrorMessage());
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendResetLink.setEnabled(false);

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSendResetLink.setEnabled(true);
                        // Navigate to success screen
                        Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordSuccessActivity.class);
                        intent.putExtra("email", email);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSendResetLink.setEnabled(true);
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Failed to send reset email: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                });
    }
}

