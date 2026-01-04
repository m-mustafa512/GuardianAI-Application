package com.mustafa.guardianai.data.repository;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Base Repository for Guardian AI
 * Provides common Firebase Firestore access
 * 
 * Repository Pattern:
 * - Separates data access logic from ViewModels
 * - Provides single source of truth for data
 * - Handles Firebase operations
 * 
 * All repositories should extend this class
 */
public abstract class BaseRepository {

    protected final FirebaseFirestore firestore;

    public BaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Get Firestore instance
     */
    @NonNull
    protected FirebaseFirestore getFirestore() {
        return firestore;
    }

    /**
     * Handle Firestore errors - override in child classes for specific error handling
     */
    protected void handleError(@NonNull Exception error) {
        // Override in child classes for specific error handling
        error.printStackTrace();
    }
}








