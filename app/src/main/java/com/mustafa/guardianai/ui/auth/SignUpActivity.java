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

        // Create Account button (only for parent accounts)
        binding.btnCreateAccount.setOnClickListener(v -> handleSignUp());

        // Login link
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
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

        // Sign up is only for parent accounts
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

