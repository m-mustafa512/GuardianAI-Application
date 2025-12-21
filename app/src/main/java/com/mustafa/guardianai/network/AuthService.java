package com.mustafa.guardianai.network;

import android.content.Context;
import android.provider.Settings;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mustafa.guardianai.data.model.User;
import com.mustafa.guardianai.data.model.UserRole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Authentication service
 * Handles user authentication, registration, and email verification
 */
public class AuthService {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public AuthService() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Get current authenticated user
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Register a new parent user
     * @param email User email
     * @param password User password
     * @param displayName Optional display name
     * @param callback Callback for result
     */
    public void registerParent(String email, String password, String displayName, 
                               AuthCallback callback) {
        // Verify Firebase is initialized
        try {
            com.google.firebase.FirebaseApp.getInstance();
        } catch (Exception e) {
            callback.onFailure(new Exception("Firebase not initialized. Please check your configuration.", e));
            return;
        }
        
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user == null) {
                            callback.onFailure(new Exception("User creation failed"));
                            return;
                        }

                        // Update display name if provided
                        if (displayName != null && !displayName.trim().isEmpty()) {
                            UserProfileChangeRequest profileUpdates = 
                                    new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();
                            user.updateProfile(profileUpdates);
                        }

                        // Send email verification
                        user.sendEmailVerification();

                        // Get device ID - use a Context from the activity/service that calls this
                        // For now, we'll get it from the Firebase app context
                        String deviceId = "unknown";
                        try {
                            deviceId = Settings.Secure.getString(
                                    auth.getApp().getApplicationContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID
                            );
                        } catch (Exception e) {
                            // Fallback if device ID cannot be retrieved
                            deviceId = java.util.UUID.randomUUID().toString();
                        }

                        // Call success callback immediately after user creation
                        // This ensures UI updates quickly
                        callback.onSuccess(user.getUid());

                        // Create user document in Firestore (in background)
                        // This doesn't block the UI
                        User userData = new User(
                                user.getUid(),
                                user.getEmail() != null ? user.getEmail() : email,
                                displayName != null ? displayName : user.getDisplayName(),
                                UserRole.PARENT,
                                null,
                                deviceId,
                                false
                        );

                        firestore.collection("users")
                                .document(user.getUid())
                                .set(userData)
                                .addOnFailureListener(e -> {
                                    // Log error but don't block UI
                                    android.util.Log.e("AuthService", "Failed to save user data to Firestore: " + e.getMessage());
                                });
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage = exception.getMessage();
                            // Provide more helpful error messages
                            if (errorMessage != null && errorMessage.contains("CONFIGURATION_NOT_FOUND")) {
                                callback.onFailure(new Exception(
                                    "Firebase Authentication is not enabled. " +
                                    "Please enable Email/Password authentication in Firebase Console: " +
                                    "https://console.firebase.google.com/project/guardian-ai-edfa6/authentication/providers"
                                ));
                            } else {
                                callback.onFailure(exception);
                            }
                        } else {
                            callback.onFailure(new Exception("Unknown error during registration"));
                        }
                    }
                });
    }

    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     * @param callback Callback for result
     */
    public void login(String email, String password, LoginCallback callback) {
        // Verify Firebase is initialized
        try {
            com.google.firebase.FirebaseApp.getInstance();
        } catch (Exception e) {
            callback.onFailure(new Exception("Firebase not initialized. Please check your configuration.", e));
            return;
        }
        
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user == null) {
                            callback.onFailure(new Exception("Login failed"));
                            return;
                        }

                        // Get user data from Firestore to determine role
                        firestore.collection("users")
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    User userData = documentSnapshot.toObject(User.class);
                                    if (userData == null || userData.getRole() == null) {
                                        callback.onFailure(new Exception("User role not found"));
                                        return;
                                    }
                                    callback.onSuccess(userData.getRole());
                                })
                                .addOnFailureListener(callback::onFailure);
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage = exception.getMessage();
                            // Provide more helpful error messages
                            if (errorMessage != null && errorMessage.contains("CONFIGURATION_NOT_FOUND")) {
                                callback.onFailure(new Exception(
                                    "Firebase Authentication is not enabled. " +
                                    "Please enable Email/Password authentication in Firebase Console: " +
                                    "https://console.firebase.google.com/project/guardian-ai-edfa6/authentication/providers"
                                ));
                            } else {
                                callback.onFailure(exception);
                            }
                        } else {
                            callback.onFailure(new Exception("Unknown error during login"));
                        }
                    }
                });
    }

    /**
     * Send email verification
     */
    public void sendEmailVerification(SimpleCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("No user logged in"));
            return;
        }

        user.sendEmailVerification()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Check if email is verified
     */
    public boolean isEmailVerified() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }

    /**
     * Reload user to get latest email verification status
     */
    public void reloadUser(SimpleCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("No user logged in"));
            return;
        }

        user.reload()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Logout current user
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * Get user data from Firestore
     */
    public void getUserData(String uid, UserDataCallback callback) {
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) {
                        callback.onFailure(new Exception("User data not found"));
                        return;
                    }
                    callback.onSuccess(user);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(String uid);
        void onFailure(Exception exception);
    }

    public interface LoginCallback {
        void onSuccess(UserRole role);
        void onFailure(Exception exception);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    public interface UserDataCallback {
        void onSuccess(User user);
        void onFailure(Exception exception);
    }
}

