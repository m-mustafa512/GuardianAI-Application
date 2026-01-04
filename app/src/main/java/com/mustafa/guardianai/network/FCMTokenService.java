package com.mustafa.guardianai.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

/**
 * FCM Token Service
 * Handles Firebase Cloud Messaging token registration and updates
 * 
 * This service:
 * - Retrieves FCM token from Firebase
 * - Stores token in Firestore linked to user UID
 * - Updates token when it refreshes
 * 
 * Note: Notification logic will be implemented in future modules
 */
public class FCMTokenService {
    private static final String TAG = "FCMTokenService";
    private static final String COLLECTION_TOKENS = "fcm_tokens";
    
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public FCMTokenService() {
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Initialize FCM token handling
     * Call this after user login to register/update token
     * 
     * Note: Firebase automatically refreshes tokens. This method gets the current token
     * and saves it. For token refresh handling, implement a FirebaseMessagingService
     * in future modules when notification logic is needed.
     */
    public void initializeToken() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "No user logged in, cannot initialize FCM token");
            return;
        }

        // Get FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Failed to get FCM token", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token retrieved: " + token);
                        
                        // Save token to Firestore
                        saveTokenToFirestore(user.getUid(), token);
                    }
                });
    }

    /**
     * Save FCM token to Firestore
     */
    private void saveTokenToFirestore(String uid, String token) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("uid", uid);
        tokenData.put("token", token);
        tokenData.put("updatedAt", System.currentTimeMillis());
        tokenData.put("platform", "Android");

        firestore.collection(COLLECTION_TOKENS)
                .document(uid)
                .set(tokenData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "FCM token saved successfully");
                        } else {
                            Log.e(TAG, "Failed to save FCM token", task.getException());
                        }
                    }
                });
    }

    /**
     * Delete FCM token from Firestore (call on logout)
     */
    public void deleteToken() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }

        firestore.collection(COLLECTION_TOKENS)
                .document(user.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "FCM token deleted successfully");
                        } else {
                            Log.e(TAG, "Failed to delete FCM token", task.getException());
                        }
                    }
                });
    }
}

