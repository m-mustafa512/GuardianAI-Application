package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.databinding.FragmentReportsBinding;
import com.mustafa.guardianai.ui.base.BaseFragment;

/**
 * Reports Fragment
 * Displays weekly and monthly summary reports
 * UI ONLY - Report generation logic in another module
 * Uses BaseFragment from Shared Foundation
 */
public class ReportsFragment extends BaseFragment {
    private FragmentReportsBinding binding;
    private String currentPeriod = "Weekly"; // "Daily", "Weekly", "Monthly"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    @Override
    protected void setupUI() {
        if (!isFragmentAttached()) return;

        // Share button
        binding.ivShare.setOnClickListener(v -> {
            showToast("Share report - coming soon");
        });

        // Time period tabs
        binding.tvTabDaily.setOnClickListener(v -> setPeriod("Daily"));
        binding.tvTabWeekly.setOnClickListener(v -> setPeriod("Weekly"));
        binding.tvTabMonthly.setOnClickListener(v -> setPeriod("Monthly"));

        // View All Apps
        binding.tvViewAllApps.setOnClickListener(v -> {
            showToast("View all apps - coming soon");
        });

        // Download Report button
        binding.btnDownloadReport.setOnClickListener(v -> {
            showToast("Download report - coming soon");
        });

        // Set default period
        setPeriod("Weekly");
    }

    /**
     * Set time period and update UI
     */
    private void setPeriod(String period) {
        currentPeriod = period;

        // Update tab selection
        binding.tvTabDaily.setBackgroundResource(period.equals("Daily") ? R.drawable.bg_filter_selected : android.R.color.transparent);
        binding.tvTabWeekly.setBackgroundResource(period.equals("Weekly") ? R.drawable.bg_filter_selected : android.R.color.transparent);
        binding.tvTabMonthly.setBackgroundResource(period.equals("Monthly") ? R.drawable.bg_filter_selected : android.R.color.transparent);

        binding.tvTabDaily.setTextColor(period.equals("Daily") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));
        binding.tvTabWeekly.setTextColor(period.equals("Weekly") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));
        binding.tvTabMonthly.setTextColor(period.equals("Monthly") ? getResources().getColor(R.color.primary) : getResources().getColor(R.color.text_secondary));

        // Set text style (bold for selected)
        binding.tvTabDaily.setTypeface(null, period.equals("Daily") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        binding.tvTabWeekly.setTypeface(null, period.equals("Weekly") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        binding.tvTabMonthly.setTypeface(null, period.equals("Monthly") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        // TODO: Load report data for selected period
        // This will be implemented when report generation module is ready
    }
}


