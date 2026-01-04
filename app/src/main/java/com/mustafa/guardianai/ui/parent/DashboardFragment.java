package com.mustafa.guardianai.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.data.model.DashboardSummary;
import com.mustafa.guardianai.databinding.FragmentDashboardBinding;
import com.mustafa.guardianai.ui.base.BaseFragment;
import java.util.List;

/**
 * Dashboard Fragment
 * Main dashboard view with child profiles and summary
 * Uses BaseFragment from Shared Foundation
 */
public class DashboardFragment extends BaseFragment {
    private FragmentDashboardBinding binding;
    private ParentDashboardViewModel viewModel;
    private ChildProfileAdapter childProfileAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(ParentDashboardViewModel.class);

        setupObservers();
        
        // Initialize dashboard data
        viewModel.initialize();
    }

    @Override
    protected void setupUI() {
        if (!isFragmentAttached()) return;
        // Set parent name from Firebase Auth
        com.mustafa.guardianai.network.AuthService authService = new com.mustafa.guardianai.network.AuthService();
        var user = authService.getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            binding.tvParentName.setText(user.getDisplayName());
        } else if (user != null && user.getEmail() != null) {
            // Extract name from email if display name not available
            String email = user.getEmail();
            int atIndex = email.indexOf('@');
            if (atIndex > 0) {
                String name = email.substring(0, atIndex);
                binding.tvParentName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        
        // Load profile picture (if available)
        loadProfilePicture();

        // Setup RecyclerView for child profiles
        childProfileAdapter = new ChildProfileAdapter(profile -> {
            // Navigate to child profile detail
            Intent intent = new Intent(requireContext(), ChildProfileDetailActivity.class);
            intent.putExtra("profileId", profile.getProfileId());
            startActivity(intent);
        });

        binding.rvChildProfiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChildProfiles.setAdapter(childProfileAdapter);

        // Add device button - launch QR code generation
        binding.cardAddDevice.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QRGenerateActivity.class);
            startActivity(intent);
        });

        // Manage All button
        binding.tvManageAll.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChildProfileListActivity.class);
            startActivity(intent);
        });

        // Notifications icon - navigate to alerts/notifications
        binding.ivNotifications.setOnClickListener(v -> {
            // Switch to Activity tab in bottom navigation
            // This will be handled by the parent activity
            if (getActivity() instanceof ParentDashboardActivity) {
                ((ParentDashboardActivity) getActivity()).navigateToAlerts();
            }
        });
    }
    
    /**
     * Load profile picture from SharedPreferences (same as SettingsFragment)
     */
    private void loadProfilePicture() {
        try {
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("GuardianAI", android.content.Context.MODE_PRIVATE);
            String base64Image = prefs.getString("parent_profile_picture", null);
            
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    binding.ivProfilePicture.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            // If loading fails, use default image
        }
    }

    private void setupObservers() {
        // Observe dashboard summary
        viewModel.getDashboardSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary != null) {
                updateDashboardSummary(summary);
            }
        });

        // Observe child profiles
        viewModel.getChildProfiles().observe(getViewLifecycleOwner(), profiles -> {
            if (profiles != null) {
                updateChildProfiles(profiles);
            }
        });
    }

    private void updateDashboardSummary(DashboardSummary summary) {
        binding.tvTotalDevices.setText(String.valueOf(summary.getTotalDevices()));
        binding.tvTotalAlerts.setText(String.valueOf(summary.getUnreadAlerts()));
        binding.tvTotalScreenTime.setText(summary.getFormattedTotalScreenTime());
    }

    private void updateChildProfiles(List<ChildProfile> profiles) {
        childProfileAdapter.updateChildProfiles(profiles);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (viewModel != null) {
            viewModel.refresh();
        }
    }
}

