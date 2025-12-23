package com.mustafa.guardianai.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.data.model.DashboardSummary;
import com.mustafa.guardianai.databinding.FragmentDashboardBinding;
import java.util.List;

/**
 * Dashboard Fragment
 * Main dashboard view with child profiles and summary
 */
public class DashboardFragment extends Fragment {
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

        setupUI();
        setupObservers();
        
        // Initialize dashboard data
        viewModel.initialize();
    }

    private void setupUI() {
        // Set parent name from Firebase Auth
        com.mustafa.guardianai.network.AuthService authService = new com.mustafa.guardianai.network.AuthService();
        var user = authService.getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            binding.tvParentName.setText(user.getDisplayName());
        } else if (user != null && user.getEmail() != null) {
            // Extract name from email if display name not available
            String email = user.getEmail();
            String name = email.substring(0, email.indexOf('@'));
            binding.tvParentName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        }

        // Setup RecyclerView for child profiles
        childProfileAdapter = new ChildProfileAdapter(profile -> {
            // Navigate to child profile detail
            Intent intent = new Intent(requireContext(), ChildProfileDetailActivity.class);
            intent.putExtra("profileId", profile.getProfileId());
            startActivity(intent);
        });

        binding.rvChildProfiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChildProfiles.setAdapter(childProfileAdapter);

        // Add device button
        binding.cardAddDevice.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChildProfileListActivity.class);
            startActivity(intent);
        });

        // Manage All button
        binding.tvManageAll.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChildProfileListActivity.class);
            startActivity(intent);
        });

        // Notifications icon
        binding.ivNotifications.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Notifications feature coming soon", Toast.LENGTH_SHORT).show();
        });
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

