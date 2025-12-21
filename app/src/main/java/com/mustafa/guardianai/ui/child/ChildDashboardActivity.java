package com.mustafa.guardianai.ui.child;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivityChildDashboardBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.auth.LoginActivity;

/**
 * Child Dashboard Activity
 * Main dashboard for child users (monitoring will be implemented here)
 */
public class ChildDashboardActivity extends AppCompatActivity {
    private ActivityChildDashboardBinding binding;
    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        var user = authService.getCurrentUser();
        binding.tvWelcome.setText("Welcome, " + (user != null && user.getEmail() != null ? user.getEmail() : "Child"));

        binding.btnLogout.setOnClickListener(v -> {
            authService.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.tvStatus.setText("Monitoring active. Guardian AI is protecting your device.");
    }
}


