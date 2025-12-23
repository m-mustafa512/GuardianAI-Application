package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mustafa.guardianai.databinding.FragmentReportsBinding;

/**
 * Reports Fragment
 * Displays weekly and monthly summary reports
 * UI ONLY - Report generation logic in another module
 */
public class ReportsFragment extends Fragment {
    private FragmentReportsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Reports UI will be implemented here
        // Actual report generation logic exists in another module
    }
}

