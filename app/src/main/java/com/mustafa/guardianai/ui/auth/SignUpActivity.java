package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivitySignupBinding;
import com.mustafa.guardianai.data.model.UserRole;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.parent.ParentDashboardActivity;
import com.mustafa.guardianai.utils.EmailValidator;
import com.mustafa.guardianai.utils.PasswordValidator;

/**
 * Sign Up Activity
 * Handles parent account creation
 */
public class SignUpActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private final AuthService authService = new AuthService();
    private UserRole selectedRole = UserRole.PARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Role toggle
        binding.btnParent.setOnClickListener(v -> {
            selectedRole = UserRole.PARENT;
            updateRoleToggle();
        });

        binding.btnChild.setOnClickListener(v -> {
            selectedRole = UserRole.CHILD;
            updateRoleToggle();
        });

        // Create Account button
        binding.btnCreateAccount.setOnClickListener(v -> handleSignUp());

        // Login link
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void updateRoleToggle() {
        if (selectedRole == UserRole.PARENT) {
            binding.btnParent.setTextColor(getResources().getColor(android.R.color.white, null));
            binding.btnParent.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, com.mustafa.guardianai.R.color.primary));
            binding.btnChild.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
            binding.btnChild.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, android.R.color.transparent));
            binding.tvInfo.setText("Parent accounts can manage child devices and settings.");
        } else {
            binding.btnChild.setTextColor(getResources().getColor(android.R.color.white, null));
            binding.btnChild.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, com.mustafa.guardianai.R.color.primary));
            binding.btnParent.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
            binding.btnParent.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, android.R.color.transparent));
            binding.tvInfo.setText("Child accounts will be paired with a parent device.");
        }
    }

    private void handleSignUp() {
        String email = binding.etSignUpEmail.getText().toString().trim();
        String password = binding.etSignUpPassword.getText().toString();
        String confirmPassword = binding.etSignUpConfirmPassword.getText().toString();
        String displayName = binding.etSignUpName.getText().toString().trim();

        // Validate email
        PasswordValidator.ValidationResult emailValidation = EmailValidator.validate(email);
        if (!emailValidation.isValid()) {
            binding.tilEmail.setError(emailValidation.getErrorMessage());
            return;
        }

        // Validate password
        PasswordValidator.ValidationResult passwordValidation = PasswordValidator.validate(password);
        if (!passwordValidation.isValid()) {
            binding.tilPassword.setError(passwordValidation.getErrorMessage());
            return;
        }

        // Check password match
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Check terms acceptance
        if (!binding.cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms and Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnCreateAccount.setEnabled(false);

        if (selectedRole == UserRole.PARENT) {
            authService.registerParent(email, password, displayName, new AuthService.AuthCallback() {
                @Override
                public void onSuccess(String uid) {
                    // Ensure we're on UI thread
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        navigateToSuccess();
                    } else {
                        runOnUiThread(() -> navigateToSuccess());
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnCreateAccount.setEnabled(true);
                        Log.e("SignUpActivity", "Sign up failed: " + exception.getMessage(), exception);
                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed: " + exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else {
            // For child, they need to be paired via QR code after signup
            Toast.makeText(this, "Child accounts must be paired via QR code after signup", Toast.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.btnCreateAccount.setEnabled(true);
        }
    }

    private void navigateToSuccess() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnCreateAccount.setEnabled(true);
        Log.d("SignUpActivity", "Navigating to SignUpSuccessActivity");
        // Navigate to success screen
        Intent intent = new Intent(SignUpActivity.this, SignUpSuccessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

