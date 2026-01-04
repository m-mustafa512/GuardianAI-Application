package com.mustafa.guardianai.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.databinding.ActivityChildProfileListBinding;
import java.util.List;

/**
 * Child Profile List Activity
 * Manages child profiles - create, view, delete
 * Full backend functionality implemented
 */
public class ChildProfileListActivity extends AppCompatActivity {
    private ActivityChildProfileListBinding binding;
    private ChildProfileViewModel viewModel;
    private ChildProfileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildProfileListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ChildProfileViewModel.class);

        setupUI();
        setupObservers();
        
        // Load child profiles
        viewModel.loadChildProfiles();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        adapter = new ChildProfileListAdapter(profile -> {
            // Navigate to child profile detail
            Intent intent = new Intent(this, ChildProfileDetailActivity.class);
            intent.putExtra("profileId", profile.getProfileId());
            startActivity(intent);
        }, profile -> {
            // Delete child profile
            showDeleteConfirmationDialog(profile);
        });

        binding.rvChildProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChildProfiles.setAdapter(adapter);

        // Add new child profile button
        binding.fabAddChild.setOnClickListener(v -> showAddChildDialog());
    }

    private void setupObservers() {
        // Observe child profiles list
        viewModel.getChildProfiles().observe(this, profiles -> {
            if (profiles != null) {
                adapter.updateChildProfiles(profiles);
                binding.tvEmptyState.setVisibility(profiles.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe operation success
        viewModel.getOperationSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Operation successful", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddChildDialog() {
        // Show options: QR Pairing or Manual Entry
        new AlertDialog.Builder(this)
                .setTitle("Add Child Profile")
                .setMessage("How would you like to add a child device?")
                .setPositiveButton("QR Code Pairing", (dialog, which) -> {
                    // Launch QR code generation activity
                    Intent intent = new Intent(this, QRGenerateActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog(ChildProfile profile) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Child Profile")
                .setMessage("Are you sure you want to delete " + profile.getName() + "'s profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteChildProfile(profile.getProfileId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh child profiles when returning to this screen
        viewModel.loadChildProfiles();
    }
}









