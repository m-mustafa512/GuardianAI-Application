package com.mustafa.guardianai.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base Fragment for Guardian AI
 * Provides common functionality for all fragments:
 * - Toast message helpers
 * - Common lifecycle handling
 * 
 * All fragments should extend this class to maintain consistency
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    /**
     * Setup UI components - override in child classes
     */
    protected abstract void setupUI();

    /**
     * Show a toast message
     */
    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show a long toast message
     */
    protected void showLongToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show error toast
     */
    protected void showError(String errorMessage) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show loading indicator (override in child classes if needed)
     */
    protected void showLoading(boolean show) {
        // Override in child classes to show/hide loading indicators
    }

    /**
     * Check if fragment is still attached to activity
     */
    protected boolean isFragmentAttached() {
        return getContext() != null && isAdded();
    }
}








