package com.mustafa.guardianai.network;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mustafa.guardianai.data.model.Alert;
import com.mustafa.guardianai.data.model.ChildProfile;
import com.mustafa.guardianai.data.model.DashboardSummary;
import java.util.List;

/**
 * Dashboard Service
 * Handles fetching and aggregating dashboard data
 */
public class DashboardService {
    private static final String TAG = "DashboardService";
    private static final String COLLECTION_DASHBOARD_SUMMARIES = "dashboard_summaries";
    private static final String COLLECTION_CHILD_PROFILES = "child_profiles";
    private static final String COLLECTION_ALERTS = "alerts";
    
    private final FirebaseFirestore firestore;

    public DashboardService() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for dashboard summary
     */
    public interface DashboardSummaryCallback {
        void onSuccess(DashboardSummary summary);
        void onFailure(Exception exception);
    }

    /**
     * Callback interface for alerts
     */
    public interface AlertsCallback {
        void onSuccess(List<Alert> alerts);
        void onFailure(Exception exception);
    }

    /**
     * Get or create dashboard summary for a parent
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for result
     */
    public void getDashboardSummary(String parentUid, DashboardSummaryCallback callback) {
        firestore.collection(COLLECTION_DASHBOARD_SUMMARIES)
                .document(parentUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DashboardSummary summary = documentToDashboardSummary(documentSnapshot);
                        if (summary != null) {
                            callback.onSuccess(summary);
                        } else {
                            // If parsing fails, create a new summary
                            createDefaultSummary(parentUid, callback);
                        }
                    } else {
                        // Create default summary if it doesn't exist
                        createDefaultSummary(parentUid, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get dashboard summary: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Create a default dashboard summary
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for result
     */
    private void createDefaultSummary(String parentUid, DashboardSummaryCallback callback) {
        DashboardSummary summary = new DashboardSummary(parentUid);
        
        // Calculate summary from child profiles and alerts
        calculateSummaryFromData(parentUid, summary, callback);
    }

    /**
     * Calculate summary by aggregating data from child profiles and alerts
     * @param parentUid Parent's Firebase UID
     * @param summary Dashboard summary to update
     * @param callback Callback for result
     */
    private void calculateSummaryFromData(String parentUid, DashboardSummary summary, 
                                          DashboardSummaryCallback callback) {
        // Get child profiles count
        firestore.collection(COLLECTION_CHILD_PROFILES)
                .whereEqualTo("parentUid", parentUid)
                .get()
                .addOnSuccessListener(profilesSnapshot -> {
                    int deviceCount = profilesSnapshot.size();
                    summary.setTotalDevices(deviceCount);
                    
                    // Calculate total screen time
                    long totalScreenTime = 0;
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : profilesSnapshot) {
                        Long screenTime = doc.getLong("screenTimeToday");
                        if (screenTime != null) {
                            totalScreenTime += screenTime;
                        }
                    }
                    summary.setTotalScreenTime(totalScreenTime);
                    
                    // Get alerts count
                    firestore.collection(COLLECTION_ALERTS)
                            .whereEqualTo("parentUid", parentUid)
                            .get()
                            .addOnSuccessListener(alertsSnapshot -> {
                                int totalAlerts = alertsSnapshot.size();
                                summary.setTotalAlerts(totalAlerts);
                                
                                // Count unread alerts
                                int unreadCount = 0;
                                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : alertsSnapshot) {
                                    Boolean isRead = doc.getBoolean("isRead");
                                    if (isRead != null && !isRead) {
                                        unreadCount++;
                                    }
                                }
                                summary.setUnreadAlerts(unreadCount);
                                
                                // Save summary to Firestore
                                saveSummary(summary, callback);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to get alerts: " + e.getMessage(), e);
                                // Still return summary even if alerts fail
                                saveSummary(summary, callback);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get child profiles: " + e.getMessage(), e);
                    // Still return summary with default values
                    saveSummary(summary, callback);
                });
    }

    /**
     * Save dashboard summary to Firestore
     * @param summary Dashboard summary to save
     * @param callback Callback for result
     */
    private void saveSummary(DashboardSummary summary, DashboardSummaryCallback callback) {
        summary.setLastUpdated(System.currentTimeMillis());
        
        firestore.collection(COLLECTION_DASHBOARD_SUMMARIES)
                .document(summary.getParentUid())
                .set(summary.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Dashboard summary saved for: " + summary.getParentUid());
                    callback.onSuccess(summary);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save dashboard summary: " + e.getMessage(), e);
                    // Still return summary even if save fails
                    callback.onSuccess(summary);
                });
    }

    /**
     * Set up real-time listener for dashboard summary
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for updates
     * @return ListenerRegistration to remove listener later
     */
    public ListenerRegistration listenToDashboardSummary(String parentUid, 
                                                         DashboardSummaryCallback callback) {
        return firestore.collection(COLLECTION_DASHBOARD_SUMMARIES)
                .document(parentUid)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error listening to dashboard summary: " + e.getMessage(), e);
                        callback.onFailure(e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        DashboardSummary summary = documentToDashboardSummary(documentSnapshot);
                        if (summary != null) {
                            callback.onSuccess(summary);
                        }
                    }
                });
    }

    /**
     * Set up real-time listener for child profiles
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for updates
     * @return ListenerRegistration to remove listener later
     */
    public ListenerRegistration listenToChildProfiles(String parentUid,
                                                       com.mustafa.guardianai.network.ChildProfileService.ChildProfileListCallback callback) {
        return firestore.collection(COLLECTION_CHILD_PROFILES)
                .whereEqualTo("parentUid", parentUid)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error listening to child profiles: " + e.getMessage(), e);
                        callback.onFailure(e);
                        return;
                    }

                    if (querySnapshot != null) {
                        java.util.List<ChildProfile> profiles = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : querySnapshot) {
                            ChildProfile profile = documentToChildProfile(document);
                            if (profile != null) {
                                profiles.add(profile);
                            }
                        }
                        callback.onSuccess(profiles);
                    }
                });
    }

    /**
     * Get unread alerts for a parent
     * @param parentUid Parent's Firebase UID
     * @param callback Callback for result
     */
    public void getUnreadAlerts(String parentUid, AlertsCallback callback) {
        firestore.collection(COLLECTION_ALERTS)
                .whereEqualTo("parentUid", parentUid)
                .whereEqualTo("isRead", false)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    java.util.List<Alert> alerts = new java.util.ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : querySnapshot) {
                        Alert alert = documentToAlert(document);
                        if (alert != null) {
                            alerts.add(alert);
                        }
                    }
                    callback.onSuccess(alerts);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get alerts: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Convert Firestore document to DashboardSummary
     */
    private DashboardSummary documentToDashboardSummary(DocumentSnapshot document) {
        try {
            DashboardSummary summary = new DashboardSummary();
            summary.setSummaryId(document.getString("summaryId"));
            summary.setParentUid(document.getString("parentUid"));
            
            Long totalDevices = document.getLong("totalDevices");
            if (totalDevices != null) {
                summary.setTotalDevices(totalDevices.intValue());
            }
            
            Long totalAlerts = document.getLong("totalAlerts");
            if (totalAlerts != null) {
                summary.setTotalAlerts(totalAlerts.intValue());
            }
            
            Long unreadAlerts = document.getLong("unreadAlerts");
            if (unreadAlerts != null) {
                summary.setUnreadAlerts(unreadAlerts.intValue());
            }
            
            Long totalScreenTime = document.getLong("totalScreenTime");
            if (totalScreenTime != null) {
                summary.setTotalScreenTime(totalScreenTime);
            }
            
            Long lastUpdated = document.getLong("lastUpdated");
            if (lastUpdated != null) {
                summary.setLastUpdated(lastUpdated);
            }
            
            return summary;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing dashboard summary: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert Firestore document to ChildProfile
     */
    private ChildProfile documentToChildProfile(com.google.firebase.firestore.QueryDocumentSnapshot document) {
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

    /**
     * Convert Firestore document to Alert
     */
    private Alert documentToAlert(com.google.firebase.firestore.QueryDocumentSnapshot document) {
        try {
            Alert alert = new Alert();
            alert.setAlertId(document.getString("alertId"));
            alert.setParentUid(document.getString("parentUid"));
            alert.setChildUid(document.getString("childUid"));
            
            String typeString = document.getString("type");
            if (typeString != null) {
                alert.setType(Alert.parseType(typeString));
            }
            
            alert.setTitle(document.getString("title"));
            alert.setMessage(document.getString("message"));
            
            String severityString = document.getString("severity");
            if (severityString != null) {
                alert.setSeverity(Alert.parseSeverity(severityString));
            }
            
            Boolean isRead = document.getBoolean("isRead");
            if (isRead != null) {
                alert.setRead(isRead);
            }
            
            Boolean isResolved = document.getBoolean("isResolved");
            if (isResolved != null) {
                alert.setResolved(isResolved);
            }
            
            Long createdAt = document.getLong("createdAt");
            if (createdAt != null) {
                alert.setCreatedAt(createdAt);
            }
            
            Long resolvedAt = document.getLong("resolvedAt");
            if (resolvedAt != null) {
                alert.setResolvedAt(resolvedAt);
            }
            
            return alert;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing alert: " + e.getMessage(), e);
            return null;
        }
    }
}

