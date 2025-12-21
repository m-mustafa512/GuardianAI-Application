package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.databinding.ActivityLoginBinding;
import com.mustafa.guardianai.data.model.UserRole;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.child.ChildDashboardActivity;
import com.mustafa.guardianai.ui.parent.ParentDashboardActivity;
import com.mustafa.guardianai.utils.EmailValidator;
import com.mustafa.guardianai.utils.PasswordValidator;

/**
 * Login Activity
 * Handles parent sign-up, login, and navigation to appropriate dashboard
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        checkExistingSession();
    }

    private void setupUI() {
        // Login button
        binding.btnLogin.setOnClickListener(v -> handleLogin());

        // Sign Up link - navigate to SignUpActivity
        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        // Forgot Password link
        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }


    private void handleLogin() {
        String email = binding.tilEmail.getEditText() != null ? 
                binding.tilEmail.getEditText().getText().toString().trim() : "";
        String password = binding.tilPassword.getEditText() != null ? 
                binding.tilPassword.getEditText().getText().toString() : "";

        // Validate email
        PasswordValidator.ValidationResult emailValidation = EmailValidator.validate(email);
        if (!emailValidation.isValid()) {
            binding.tilEmail.setError(emailValidation.getErrorMessage());
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password cannot be empty");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        authService.login(email, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess(UserRole role) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);

                    // Check email verification for parent
                    if (role == UserRole.PARENT) {
                        authService.reloadUser(new AuthService.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                if (!authService.isEmailVerified()) {
                                    // Navigate to email verification screen
                                    startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class));
                                    finish();
                                    return;
                                }
                                navigateToDashboard(role);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                navigateToDashboard(role);
                            }
                        });
                    } else {
                        navigateToDashboard(role);
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    private void navigateToDashboard(UserRole role) {
        Intent intent;
        if (role == UserRole.PARENT) {
            intent = new Intent(this, ParentDashboardActivity.class);
        } else {
            intent = new Intent(this, ChildDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void checkExistingSession() {
        if (authService.isAuthenticated()) {
            com.google.firebase.auth.FirebaseUser user = authService.getCurrentUser();
            if (user != null) {
                authService.getUserData(user.getUid(), new AuthService.UserDataCallback() {
                    @Override
                    public void onSuccess(com.mustafa.guardianai.data.model.User user) {
                        runOnUiThread(() -> navigateToDashboard(user.getRole()));
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // Session invalid, stay on login screen
                    }
                });
            }
        }
    }
}

