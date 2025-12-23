package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.databinding.ActivityParentDashboardBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.utils.BiometricHelper;

/**
 * Parent Dashboard Activity
 * Main activity with bottom navigation and fragment container
 * Biometric authentication only triggers when app is reopened from background
 */
public class ParentDashboardActivity extends AppCompatActivity {
    private ActivityParentDashboardBinding binding;
    private final AuthService authService = new AuthService();
    private BiometricHelper biometricHelper;
    private boolean isAppInBackground = false;
    private boolean hasCheckedBiometricOnStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        biometricHelper = new BiometricHelper(this);
        
        setupBottomNavigation();
        
        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            
            if (itemId == R.id.nav_home) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_activity) {
                // Activity feature coming soon
                Toast.makeText(this, "Activity feature coming soon", Toast.LENGTH_SHORT).show();
                return false;
            } else if (itemId == R.id.nav_reports) {
                fragment = new ReportsFragment();
            } else if (itemId == R.id.nav_settings) {
                fragment = new SettingsFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

        // Set home as selected
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /**
     * Check biometric authentication
     * Only called when app is resumed from background
     */
    private void checkBiometricAndAuthenticate() {
        // Don't check if already checked or if app wasn't in background
        if (!isAppInBackground || hasCheckedBiometricOnStart) {
            return;
        }
        
        hasCheckedBiometricOnStart = true;
        
        BiometricHelper.BiometricStatus status = biometricHelper.getBiometricStatus();
        switch (status) {
            case AVAILABLE:
                biometricHelper.showBiometricPrompt(
                        this,
                        "Unlock Guardian AI",
                        "Use your fingerprint or face to access the dashboard",
                        new BiometricHelper.BiometricCallback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> {
                                    // Authentication successful, allow access
                                    isAppInBackground = false;
                                });
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ParentDashboardActivity.this,
                                            error,
                                            Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                );
                break;
            case NOT_ENROLLED:
                // No biometric enrolled, allow access anyway
                isAppInBackground = false;
                break;
            default:
                // Biometric not available, allow access anyway
                isAppInBackground = false;
                break;
        }
    }

    /**
     * Called when the user explicitly leaves the activity
     * (e.g., pressing home button, switching to another app)
     * This is NOT called when navigating within the app or showing dialogs
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // User is leaving the app, mark as in background
        isAppInBackground = true;
        hasCheckedBiometricOnStart = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Only check biometric if app was in background (user left the app)
        if (isAppInBackground) {
            checkBiometricAndAuthenticate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't set isAppInBackground here - only onUserLeaveHint() should do that
        // onStop() is called even when navigating within the app
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't set isAppInBackground here - only onUserLeaveHint() should do that
        // onPause() is called even when navigating within the app or showing dialogs
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset the check flag when resuming (in case user dismissed biometric)
        // But don't check biometric here - only check in onStart()
    }
}
