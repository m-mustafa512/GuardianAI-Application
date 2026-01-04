package com.mustafa.guardianai.ui.base;

import androidx.lifecycle.ViewModel;

/**
 * Base ViewModel for Guardian AI
 * Provides common functionality for all ViewModels:
 * - Error handling
 * - Loading state management
 * 
 * All ViewModels should extend this class to maintain consistency
 */
public abstract class BaseViewModel extends ViewModel {

    /**
     * Called when ViewModel is cleared
     * Override to clean up resources
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // Override in child classes to clean up resources
    }

    /**
     * Handle error - override in child classes for specific error handling
     */
    protected void handleError(Exception error) {
        // Override in child classes for specific error handling
    }
}








