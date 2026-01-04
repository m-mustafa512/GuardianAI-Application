package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.Alert;
import com.mustafa.guardianai.databinding.FragmentAlertsBinding;
import com.mustafa.guardianai.network.DashboardService;
import com.mustafa.guardianai.ui.base.BaseFragment;
import java.util.List;

/**
 * Alerts Fragment
 * Displays notifications and alerts for the parent
 * Uses BaseFragment from Shared Foundation
 */
public class AlertsFragment extends BaseFragment {
    private FragmentAlertsBinding binding;
    private AlertsAdapter alertsAdapter;
    private DashboardService dashboardService;
    private String currentFilter = "All"; // "All", "Unread", "Resolved"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dashboardService = new DashboardService();
        setupUI();
        loadAlerts();
    }

    @Override
    protected void setupUI() {
        if (!isFragmentAttached()) return;

        // Setup RecyclerView
        alertsAdapter = new AlertsAdapter();
        binding.rvAlerts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAlerts.setAdapter(alertsAdapter);

        // Mark all read button
        binding.tvMarkAllRead.setOnClickListener(v -> markAllAlertsRead());

        // Filter tabs
        binding.tvFilterAll.setOnClickListener(v -> setFilter("All"));
        binding.tvFilterUnread.setOnClickListener(v -> setFilter("Unread"));
        binding.tvFilterResolved.setOnClickListener(v -> setFilter("Resolved"));

        // Set default filter
        setFilter("All");
    }

    /**
     * Set filter and update UI
     */
    private void setFilter(String filter) {
        currentFilter = filter;
        
        // Update tab selection
        binding.tvFilterAll.setBackgroundResource(filter.equals("All") ? R.drawable.bg_filter_selected : android.R.color.transparent);
        binding.tvFilterUnread.setBackgroundResource(filter.equals("Unread") ? R.drawable.bg_filter_selected : android.R.color.transparent);
        binding.tvFilterResolved.setBackgroundResource(filter.equals("Resolved") ? R.drawable.bg_filter_selected : android.R.color.transparent);
        
        binding.tvFilterAll.setTextColor(filter.equals("All") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));
        binding.tvFilterUnread.setTextColor(filter.equals("Unread") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));
        binding.tvFilterResolved.setTextColor(filter.equals("Resolved") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));
        
        // Reload alerts with new filter
        loadAlerts();
    }

    /**
     * Load alerts from Firebase
     */
    private void loadAlerts() {
        if (!isFragmentAttached()) return;

        com.mustafa.guardianai.network.AuthService authService = new com.mustafa.guardianai.network.AuthService();
        var user = authService.getCurrentUser();
        if (user == null) {
            showError("Not authenticated");
            return;
        }

        showLoading(true);

        // Load all alerts (filtering will be done client-side for simplicity)
        dashboardService.getAllAlerts(user.getUid(), new DashboardService.AlertsCallback() {
            @Override
            public void onSuccess(List<Alert> alerts) {
                if (!isFragmentAttached()) return;
                
                showLoading(false);
                
                // Filter alerts based on current filter
                List<Alert> filteredAlerts = filterAlerts(alerts);
                alertsAdapter.updateAlerts(filteredAlerts);
            }

            @Override
            public void onFailure(Exception exception) {
                if (!isFragmentAttached()) return;
                
                showLoading(false);
                showError("Failed to load alerts: " + exception.getMessage());
            }
        });
    }

    /**
     * Filter alerts based on current filter selection
     */
    private List<Alert> filterAlerts(List<Alert> alerts) {
        if (currentFilter.equals("All")) {
            return alerts;
        } else if (currentFilter.equals("Unread")) {
            return alerts.stream()
                    .filter(alert -> !alert.isRead())
                    .collect(java.util.stream.Collectors.toList());
        } else { // Resolved
            return alerts.stream()
                    .filter(Alert::isResolved)
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    /**
     * Mark all alerts as read
     */
    private void markAllAlertsRead() {
        // TODO: Implement mark all as read functionality
        showToast("Mark all as read - coming soon");
    }

    @Override
    protected void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh alerts when fragment becomes visible
        loadAlerts();
    }
}

