package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivitySplashBinding;
import com.mustafa.guardianai.network.AuthService;

/**
 * Splash Activity
 * Shows app logo and tagline, then navigates to role selection or dashboard
 */
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private final AuthService authService = new AuthService();
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("PKG_CHECK", getPackageName());

        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Animate progress bar
        animateProgressBar();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkAuthAndNavigate();
        }, SPLASH_DURATION);
    }

    private void animateProgressBar() {
        // Simulate progress bar animation
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                final int progress = i;
                runOnUiThread(() -> binding.progressBar.setProgress(progress));
                try {
                    Thread.sleep(SPLASH_DURATION / 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkAuthAndNavigate() {
        if (authService.isAuthenticated()) {
            com.google.firebase.auth.FirebaseUser user = authService.getCurrentUser();
            if (user != null) {
                authService.getUserData(user.getUid(), new AuthService.UserDataCallback() {
                    @Override
                    public void onSuccess(com.mustafa.guardianai.data.model.User user) {
                        // User is logged in, navigate to appropriate dashboard
                        Intent intent;
                        if (user.getRole() == com.mustafa.guardianai.data.model.UserRole.PARENT) {
                            intent = new Intent(SplashActivity.this, com.mustafa.guardianai.ui.parent.ParentDashboardActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, com.mustafa.guardianai.ui.child.ChildDashboardActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // Navigate to role selection
                        navigateToRoleSelection();
                    }
                });
            } else {
                navigateToRoleSelection();
            }
        } else {
            navigateToRoleSelection();
        }
    }

    private void navigateToRoleSelection() {
        startActivity(new Intent(this, RoleSelectionActivity.class));
        finish();
    }
}

