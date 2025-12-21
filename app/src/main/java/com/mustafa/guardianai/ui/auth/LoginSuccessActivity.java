package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivityLoginSuccessBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.parent.ParentDashboardActivity;
import com.mustafa.guardianai.ui.child.ChildDashboardActivity;
import com.mustafa.guardianai.data.model.UserRole;

/**
 * Login Success Activity
 * Shows success message and redirects to dashboard
 */
public class LoginSuccessActivity extends AppCompatActivity {
    private ActivityLoginSuccessBinding binding;
    private final AuthService authService = new AuthService();
    private static final int REDIRECT_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        autoRedirect();
    }

    private void setupUI() {
        // Go to Dashboard button
        binding.btnGoToDashboard.setOnClickListener(v -> navigateToDashboard());
    }

    private void autoRedirect() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToDashboard();
        }, REDIRECT_DELAY);
    }

    private void navigateToDashboard() {
        com.google.firebase.auth.FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            authService.getUserData(user.getUid(), new AuthService.UserDataCallback() {
                @Override
                public void onSuccess(com.mustafa.guardianai.data.model.User user) {
                    Intent intent;
                    if (user.getRole() == UserRole.PARENT) {
                        intent = new Intent(LoginSuccessActivity.this, ParentDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginSuccessActivity.this, ChildDashboardActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception exception) {
                    // Default to parent dashboard if role can't be determined
                    startActivity(new Intent(LoginSuccessActivity.this, ParentDashboardActivity.class));
                    finish();
                }
            });
        } else {
            // No user, go back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}

