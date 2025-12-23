package com.mustafa.guardianai.ui.parent;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.data.model.DashboardSummary;
import com.mustafa.guardianai.network.ChildProfileService;
import com.mustafa.guardianai.network.DashboardService;
import java.util.List;

/**
 * ViewModel for Parent Dashboard
 * Manages dashboard data and business logic
 */
public class ParentDashboardViewModel extends AndroidViewModel {
    private static final String TAG = "ParentDashboardViewModel";
    
    private final DashboardService dashboardService;
    private final ChildProfileService childProfileService;
    private final FirebaseAuth auth;
    
    // LiveData for dashboard summary
    private final MutableLiveData<DashboardSummary> dashboardSummary = new MutableLiveData<>();
    
    // LiveData for child profiles list
    private final MutableLiveData<List<ChildProfile>> childProfiles = new MutableLiveData<>();
    
    // LiveData for loading state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // LiveData for error messages
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // Firestore listeners
    private ListenerRegistration summaryListener;
    private ListenerRegistration profilesListener;

    public ParentDashboardViewModel(@NonNull Application application) {
        super(application);
        this.dashboardService = new DashboardService();
        this.childProfileService = new ChildProfileService();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Initialize and start listening to dashboard data
     */
    public void initialize() {
        String parentUid = getCurrentParentUid();
        if (parentUid == null || parentUid.isEmpty()) {
            errorMessage.setValue("Not authenticated");
            return;
        }

        isLoading.setValue(true);
        
        // Load dashboard summary
        loadDashboardSummary(parentUid);
        
        // Load child profiles
        loadChildProfiles(parentUid);
        
        // Set up real-time listeners
        setupRealtimeListeners(parentUid);
    }

    /**
     * Load dashboard summary
     */
    private void loadDashboardSummary(String parentUid) {
        dashboardService.getDashboardSummary(parentUid, new DashboardService.DashboardSummaryCallback() {
            @Override
            public void onSuccess(DashboardSummary summary) {
                dashboardSummary.postValue(summary);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to load dashboard summary: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to load dashboard: " + exception.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Load child profiles
     */
    private void loadChildProfiles(String parentUid) {
        childProfileService.getChildProfilesByParent(parentUid, new ChildProfileService.ChildProfileListCallback() {
            @Override
            public void onSuccess(List<ChildProfile> profiles) {
                childProfiles.postValue(profiles);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to load child profiles: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to load child profiles: " + exception.getMessage());
            }
        });
    }

    /**
     * Set up real-time listeners for dashboard updates
     */
    private void setupRealtimeListeners(String parentUid) {
        // Listen to dashboard summary changes
        summaryListener = dashboardService.listenToDashboardSummary(parentUid, 
                new DashboardService.DashboardSummaryCallback() {
                    @Override
                    public void onSuccess(DashboardSummary summary) {
                        dashboardSummary.postValue(summary);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "Error in summary listener: " + exception.getMessage(), exception);
                    }
                });

        // Listen to child profiles changes
        profilesListener = dashboardService.listenToChildProfiles(parentUid,
                new ChildProfileService.ChildProfileListCallback() {
                    @Override
                    public void onSuccess(List<ChildProfile> profiles) {
                        childProfiles.postValue(profiles);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "Error in profiles listener: " + exception.getMessage(), exception);
                    }
                });
    }

    /**
     * Refresh dashboard data
     */
    public void refresh() {
        String parentUid = getCurrentParentUid();
        if (parentUid != null && !parentUid.isEmpty()) {
            isLoading.setValue(true);
            loadDashboardSummary(parentUid);
            loadChildProfiles(parentUid);
        }
    }

    /**
     * Get current parent UID
     */
    private String getCurrentParentUid() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    /**
     * Clean up listeners when ViewModel is cleared
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (summaryListener != null) {
            summaryListener.remove();
        }
        if (profilesListener != null) {
            profilesListener.remove();
        }
    }

    // Getters for LiveData
    public LiveData<DashboardSummary> getDashboardSummary() {
        return dashboardSummary;
    }

    public LiveData<List<ChildProfile>> getChildProfiles() {
        return childProfiles;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}

