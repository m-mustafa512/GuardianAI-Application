package com.mustafa.guardianai;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Application class
 * Initializes Firebase and other app-wide components
 */
public class GuardianAIApplication extends Application {
    private static final String TAG = "GuardianAIApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        // This ensures Firebase is initialized before any Firebase calls
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "Firebase initialized successfully");
            } else {
                Log.d(TAG, "Firebase already initialized");
            }
            
            // Verify Firebase is properly configured
            FirebaseApp app = FirebaseApp.getInstance();
            FirebaseOptions options = app.getOptions();
            Log.d(TAG, "Firebase Project ID: " + options.getProjectId());
            Log.d(TAG, "Firebase Application ID: " + options.getApplicationId());
            
            // Ensure Firebase Auth persistence is enabled (it's enabled by default)
            // Firebase Auth automatically persists user sessions across app restarts
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Log.d(TAG, "Firebase Auth initialized - sessions will persist automatically");
            
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage(), e);
        }
    }
}

