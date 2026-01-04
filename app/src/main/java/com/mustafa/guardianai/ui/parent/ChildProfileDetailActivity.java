package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.databinding.ActivityChildProfileDetailBinding;

/**
 * Child Profile Detail Activity
 * Displays detailed information about a child profile
 */
public class ChildProfileDetailActivity extends AppCompatActivity {
    private ActivityChildProfileDetailBinding binding;
    private ChildProfileViewModel viewModel;
    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get profile ID from intent
        profileId = getIntent().getStringExtra("profileId");
        if (profileId == null || profileId.isEmpty()) {
            Toast.makeText(this, "Invalid child profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ChildProfileViewModel.class);

        setupUI();
        setupObservers();
        
        // Load child profile
        viewModel.loadChildProfile(profileId);
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEditProfile.setOnClickListener(v -> {
            // Edit profile functionality (to be implemented)
            Toast.makeText(this, "Edit profile feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        viewModel.getChildProfile().observe(this, profile -> {
            if (profile != null) {
                displayChildProfile(profile);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayChildProfile(ChildProfile profile) {
        binding.tvChildName.setText(profile.getName());
        binding.tvAge.setText(profile.getAge() + " Years Old");
        binding.tvDeviceName.setText(profile.getDeviceName());
        
        // Screen time
        binding.tvScreenTimeToday.setText(profile.getFormattedScreenTime());
        binding.tvScreenTimeLimit.setText(profile.getFormattedScreenTimeLimit());
        
        // Status
        if (profile.isOnline()) {
            binding.tvStatus.setText("Online");
        } else {
            binding.tvStatus.setText("Offline");
        }
        
        // Location
        if (profile.getCurrentLocation() != null && !profile.getCurrentLocation().isEmpty()) {
            binding.tvLocation.setText(profile.getCurrentLocation());
        } else {
            binding.tvLocation.setText("Not available");
        }
    }
}









