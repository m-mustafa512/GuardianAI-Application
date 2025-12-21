package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivityRoleSelectionBinding;

/**
 * Role Selection Activity
 * Allows user to choose between Parent and Child role
 */
public class RoleSelectionActivity extends AppCompatActivity {
    private ActivityRoleSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        // Parent card - navigate to login (parents need to sign up/login first)
        binding.cardParent.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        // Child card - navigate to login (children can login or scan QR)
        binding.cardChild.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}

