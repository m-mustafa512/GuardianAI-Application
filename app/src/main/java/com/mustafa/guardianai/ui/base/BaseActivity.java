package com.mustafa.guardianai.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * Base Activity for Guardian AI
 * Provides common functionality for all activities:
 * - Toast message helpers
 * - Loading state management
 * - Common lifecycle handling
 * 
 * All activities should extend this class to maintain consistency
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a long toast message
     */
    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show error toast
     */
    protected void showError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Show loading indicator (override in child classes if needed)
     */
    protected void showLoading(boolean show) {
        // Override in child classes to show/hide loading indicators
    }
}








