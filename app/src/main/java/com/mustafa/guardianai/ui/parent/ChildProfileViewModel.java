package com.mustafa.guardianai.ui.parent;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.network.ChildProfileService;
import java.util.List;

/**
 * ViewModel for Child Profile Management
 * Handles CRUD operations for child profiles
 */
public class ChildProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ChildProfileViewModel";
    
    private final ChildProfileService childProfileService;
    private final FirebaseAuth auth;
    
    // LiveData for child profiles list
    private final MutableLiveData<List<ChildProfile>> childProfiles = new MutableLiveData<>();
    
    // LiveData for single child profile (for detail view)
    private final MutableLiveData<ChildProfile> childProfile = new MutableLiveData<>();
    
    // LiveData for loading state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // LiveData for operation success
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    
    // LiveData for error messages
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ChildProfileViewModel(@NonNull Application application) {
        super(application);
        this.childProfileService = new ChildProfileService();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Load all child profiles for current parent
     */
    public void loadChildProfiles() {
        String parentUid = getCurrentParentUid();
        if (parentUid == null || parentUid.isEmpty()) {
            errorMessage.setValue("Not authenticated");
            return;
        }

        isLoading.setValue(true);
        
        childProfileService.getChildProfilesByParent(parentUid, new ChildProfileService.ChildProfileListCallback() {
            @Override
            public void onSuccess(List<ChildProfile> profiles) {
                childProfiles.postValue(profiles);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to load child profiles: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to load child profiles: " + exception.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Load a specific child profile by profile ID
     */
    public void loadChildProfile(String profileId) {
        isLoading.setValue(true);
        
        childProfileService.getChildProfile(profileId, new ChildProfileService.ChildProfileCallback() {
            @Override
            public void onSuccess(ChildProfile profile) {
                childProfile.postValue(profile);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to load child profile: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to load child profile: " + exception.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Load a child profile by child UID
     */
    public void loadChildProfileByUid(String childUid) {
        isLoading.setValue(true);
        
        childProfileService.getChildProfileByUid(childUid, new ChildProfileService.ChildProfileCallback() {
            @Override
            public void onSuccess(ChildProfile profile) {
                childProfile.postValue(profile);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to load child profile by UID: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to load child profile: " + exception.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Create a new child profile
     */
    public void createChildProfile(String childUid, String name, int age, 
                                   String deviceName, String deviceType) {
        String parentUid = getCurrentParentUid();
        if (parentUid == null || parentUid.isEmpty()) {
            errorMessage.setValue("Not authenticated");
            return;
        }

        isLoading.setValue(true);
        
        childProfileService.createChildProfile(parentUid, childUid, name, age, deviceName, deviceType,
                new ChildProfileService.ChildProfileCallback() {
                    @Override
                    public void onSuccess(ChildProfile profile) {
                        operationSuccess.postValue(true);
                        isLoading.postValue(false);
                        // Reload profiles list
                        loadChildProfiles();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "Failed to create child profile: " + exception.getMessage(), exception);
                        errorMessage.postValue("Failed to create child profile: " + exception.getMessage());
                        operationSuccess.postValue(false);
                        isLoading.postValue(false);
                    }
                });
    }

    /**
     * Update an existing child profile
     */
    public void updateChildProfile(ChildProfile profile) {
        isLoading.setValue(true);
        
        childProfileService.updateChildProfile(profile, new ChildProfileService.SimpleCallback() {
            @Override
            public void onSuccess() {
                operationSuccess.postValue(true);
                isLoading.postValue(false);
                // Update the profile in LiveData
                childProfile.postValue(profile);
                // Reload profiles list
                loadChildProfiles();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to update child profile: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to update child profile: " + exception.getMessage());
                operationSuccess.postValue(false);
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Delete a child profile
     */
    public void deleteChildProfile(String profileId) {
        isLoading.setValue(true);
        
        childProfileService.deleteChildProfile(profileId, new ChildProfileService.SimpleCallback() {
            @Override
            public void onSuccess() {
                operationSuccess.postValue(true);
                isLoading.postValue(false);
                // Reload profiles list
                loadChildProfiles();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Failed to delete child profile: " + exception.getMessage(), exception);
                errorMessage.postValue("Failed to delete child profile: " + exception.getMessage());
                operationSuccess.postValue(false);
                isLoading.postValue(false);
            }
        });
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

    // Getters for LiveData
    public LiveData<List<ChildProfile>> getChildProfiles() {
        return childProfiles;
    }

    public LiveData<ChildProfile> getChildProfile() {
        return childProfile;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}

