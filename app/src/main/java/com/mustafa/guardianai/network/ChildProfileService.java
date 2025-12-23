package com.mustafa.guardianai.network;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mustafa.guardianai.data.model.ChildProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Child Profile Service
 * Handles CRUD operations for child profiles in Firestore
 */
public class ChildProfileService {
    private static final String TAG = "ChildProfileService";
    private static final String COLLECTION_CHILD_PROFILES = "child_profiles";
    private static final String COLLECTION_DEVICE_PAIRS = "device_pairs";
    
    private final FirebaseFirestore firestore;

    public ChildProfileService() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for child profile operations
     */
    public interface ChildProfileCallback {
        void onSuccess(ChildProfile profile);
        void onFailure(Exception exception);
    }

    /**
     * Callback interface for list operations
     */
    public interface ChildProfileListCallback {
        void onSuccess(List<ChildProfile> profiles);
        void onFailure(Exception exception);
    }

    /**
     * Callback interface for simple operations
     */
    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    /**
     * Create a new child profile
     * @param parentUid Parent's Firebase UID
     * @param childUid Child's Firebase UID (from paired device)
     * @param name Child's name
     * @param age Child's age
     * @param deviceName Device name (e.g., "iPhone 13")
     * @param deviceType Device type ("iOS" or "Android")
     * @param callback Callback for result
     */
    public void createChildProfile(String parentUid, String childUid, String name, int age,
                                   String deviceName, String deviceType, ChildProfileCallback callback) {
        try {
            String profileId = UUID.randomUUID().toString();
            ChildProfile profile = new ChildProfile(profileId, childUid, parentUid, name, age);
            profile.setDeviceName(deviceName);
            profile.setDeviceType(deviceType);
            profile.setUpdatedAt(System.currentTimeMillis());

            firestore.collection(COLLECTION_CHILD_PROFILES)
                    .document(profileId)
                    .set(profile.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Child profile created: " + profileId);
                        callback.onSuccess(profile);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create child profile: " + e.getMessage(), e);
                        callback.onFailure(e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error creating child profile: " + e.getMessage(), e);
            callback.onFailure(e);
        }
    }

    /**
     * Get a child profile by profile ID
     * @param profileId Profile ID
     * @param callback Callback for result
     */
    public void getChildProfile(String profileId, ChildProfileCallback callback) {
        firestore.collection(COLLECTION_CHILD_PROFILES)
                .document(profileId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ChildProfile profile = documentToChildProfile(documentSnapshot);
                        if (profile != null) {
                            callback.onSuccess(profile);
                        } else {
                            callback.onFailure(new Exception("Failed to parse child profile"));
                        }
                    } else {
                        callback.onFailure(new Exception("Child profile not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get child profile: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Get a child profile by child UID
     * @param childUid Child's Firebase UID
     * @param callback Callback for result
     */
    public void getChildProfileByUid(String childUid, ChildProfileCallback callback) {
        firestore.collection(COLLECTION_CHILD_PROFILES)
                .whereEqualTo("childUid", childUid)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        ChildProfile profile = documentToChildProfile(doc);
                        if (profile != null) {
                            callback.onSuccess(profile);
                        } else {
                            callback.onFailure(new Exception("Failed to parse child profile"));
                        }
                    } else {
                        callback.onFailure(new Exception("Child profile not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get child profile by UID: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Get all child profiles for a parent
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for result
     */
    public void getChildProfilesByParent(String parentUid, ChildProfileListCallback callback) {
        firestore.collection(COLLECTION_CHILD_PROFILES)
                .whereEqualTo("parentUid", parentUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ChildProfile> profiles = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        ChildProfile profile = documentToChildProfile(document);
                        if (profile != null) {
                            profiles.add(profile);
                        }
                    }
                    Log.d(TAG, "Retrieved " + profiles.size() + " child profiles for parent: " + parentUid);
                    callback.onSuccess(profiles);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get child profiles: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Update a child profile
     * @param profile Updated child profile
     * @param callback Callback for result
     */
    public void updateChildProfile(ChildProfile profile, SimpleCallback callback) {
        if (profile.getProfileId() == null || profile.getProfileId().isEmpty()) {
            callback.onFailure(new Exception("Profile ID is required"));
            return;
        }

        profile.setUpdatedAt(System.currentTimeMillis());

        firestore.collection(COLLECTION_CHILD_PROFILES)
                .document(profile.getProfileId())
                .set(profile.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Child profile updated: " + profile.getProfileId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update child profile: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Delete a child profile
     * Also deletes associated device pairs
     * @param profileId Profile ID to delete
     * @param callback Callback for result
     */
    public void deleteChildProfile(String profileId, SimpleCallback callback) {
        // First, get the profile to find childUid
        firestore.collection(COLLECTION_CHILD_PROFILES)
                .document(profileId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String childUid = documentSnapshot.getString("childUid");
                        
                        // Delete the profile
                        firestore.collection(COLLECTION_CHILD_PROFILES)
                                .document(profileId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Child profile deleted: " + profileId);
                                    
                                    // Delete associated device pairs
                                    if (childUid != null && !childUid.isEmpty()) {
                                        deleteDevicePairs(childUid, callback);
                                    } else {
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to delete child profile: " + e.getMessage(), e);
                                    callback.onFailure(e);
                                });
                    } else {
                        callback.onFailure(new Exception("Child profile not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get child profile for deletion: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Delete device pairs associated with a child UID
     * @param childUid Child's Firebase UID
     * @param callback Callback for result
     */
    private void deleteDevicePairs(String childUid, SimpleCallback callback) {
        firestore.collection(COLLECTION_DEVICE_PAIRS)
                .whereEqualTo("childUid", childUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess();
                        return;
                    }

                    // Delete all pairs
                    int totalPairs = querySnapshot.size();
                    final int[] deletedCount = {0};
                    
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        document.getReference()
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    deletedCount[0]++;
                                    if (deletedCount[0] == totalPairs) {
                                        Log.d(TAG, "Deleted " + totalPairs + " device pairs for child: " + childUid);
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to delete device pair: " + e.getMessage(), e);
                                    // Continue deleting others even if one fails
                                    deletedCount[0]++;
                                    if (deletedCount[0] == totalPairs) {
                                        callback.onSuccess(); // Still call success if most are deleted
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to query device pairs: " + e.getMessage(), e);
                    // Don't fail the whole operation if we can't delete pairs
                    callback.onSuccess();
                });
    }

    /**
     * Convert Firestore document to ChildProfile object
     * @param document Firestore document
     * @return ChildProfile object or null if parsing fails
     */
    private ChildProfile documentToChildProfile(DocumentSnapshot document) {
        try {
            ChildProfile profile = new ChildProfile();
            profile.setProfileId(document.getString("profileId"));
            profile.setChildUid(document.getString("childUid"));
            profile.setParentUid(document.getString("parentUid"));
            profile.setName(document.getString("name"));
            
            Long age = document.getLong("age");
            if (age != null) {
                profile.setAge(age.intValue());
            }
            
            profile.setDeviceName(document.getString("deviceName"));
            profile.setDeviceType(document.getString("deviceType"));
            profile.setProfilePictureUrl(document.getString("profilePictureUrl"));
            
            Boolean isOnline = document.getBoolean("isOnline");
            if (isOnline != null) {
                profile.setOnline(isOnline);
            }
            
            Long lastSeen = document.getLong("lastSeen");
            if (lastSeen != null) {
                profile.setLastSeen(lastSeen);
            }
            
            profile.setCurrentLocation(document.getString("currentLocation"));
            
            Long screenTimeLimit = document.getLong("screenTimeLimit");
            if (screenTimeLimit != null) {
                profile.setScreenTimeLimit(screenTimeLimit);
            }
            
            Long screenTimeToday = document.getLong("screenTimeToday");
            if (screenTimeToday != null) {
                profile.setScreenTimeToday(screenTimeToday);
            }
            
            Long screenTimePercentage = document.getLong("screenTimePercentage");
            if (screenTimePercentage != null) {
                profile.setScreenTimePercentage(screenTimePercentage.intValue());
            }
            
            Long createdAt = document.getLong("createdAt");
            if (createdAt != null) {
                profile.setCreatedAt(createdAt);
            }
            
            Long updatedAt = document.getLong("updatedAt");
            if (updatedAt != null) {
                profile.setUpdatedAt(updatedAt);
            }
            
            return profile;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing child profile: " + e.getMessage(), e);
            return null;
        }
    }
}

