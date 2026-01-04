package com.mustafa.guardianai.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mustafa.guardianai.data.model.QRPairingData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * QR Pairing Service
 * Handles secure password-less pairing of child devices via QR code
 */
public class QRPairingService {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private static final long PAIRING_EXPIRY_TIME = 5 * 60 * 1000L; // 5 minutes

    public QRPairingService() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Generate QR pairing data for parent device
     * This creates a temporary pairing token that child can scan
     */
    public void generatePairingQR(String parentUid, String parentEmail, QRPairingCallback callback) {
        String pairToken = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + PAIRING_EXPIRY_TIME;

        QRPairingData pairingData = new QRPairingData(
                parentUid,
                parentEmail,
                pairToken,
                expiresAt
        );

        // Store pairing token in Firestore with expiration
        Map<String, Object> tokenData = pairingData.toMap();
        tokenData.put("createdAt", System.currentTimeMillis()); // Explicit createdAt field
        
        firestore.collection("pairing_tokens")
                .document(pairToken)
                .set(tokenData)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("QRPairingService", "Pairing token stored successfully: " + pairToken);
                    callback.onSuccess(pairingData);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("QRPairingService", "Failed to store pairing token: " + e.getMessage(), e);
                    callback.onFailure(e);
                });
    }

    /**
     * Validate and process QR pairing data from child device
     * Assumes child is already authenticated anonymously
     * @param qrData QR pairing data scanned by child
     * @param callback Callback for result
     */
    public void processPairing(QRPairingData qrData, SimpleCallback callback) {
        android.util.Log.d("QRPairingService", "Starting pairing process - Token: " + qrData.getPairToken());
        
        // Get authenticated child user
        FirebaseUser childUser = auth.getCurrentUser();
        if (childUser == null || !childUser.isAnonymous()) {
            android.util.Log.e("QRPairingService", "Child user not authenticated anonymously");
            callback.onFailure(new Exception("Child device must be authenticated"));
            return;
        }
        
        String childUid = childUser.getUid();
        
        // Read pairing token from Firestore
        firestore.collection("pairing_tokens")
                .document(qrData.getPairToken())
                .get()
                .addOnSuccessListener(tokenDoc -> {
                    // Fail if token does not exist
                    if (!tokenDoc.exists()) {
                        android.util.Log.e("QRPairingService", "Token does not exist: " + qrData.getPairToken());
                        callback.onFailure(new Exception("Invalid pairing token. Please scan a fresh QR code."));
                        return;
                    }
                    
                    // Check expiration
                    Long expiresAt = tokenDoc.getLong("expiresAt");
                    if (expiresAt == null || System.currentTimeMillis() > expiresAt) {
                        android.util.Log.e("QRPairingService", "Token expired");
                        callback.onFailure(new Exception("Pairing token has expired. Please generate a new QR code."));
                        return;
                    }
                    
                    // Get parent UID from Firestore - this is the single source of truth
                    String storedParentUid = tokenDoc.getString("parentUid");
                    if (storedParentUid == null) {
                        android.util.Log.e("QRPairingService", "Invalid token data - missing parentUid");
                        callback.onFailure(new Exception("Invalid pairing data"));
                        return;
                    }
                    
                    // Create device_pairs document using parentUid from Firestore
                    String pairId = UUID.randomUUID().toString();
                    Map<String, Object> pairData = new HashMap<>();
                    pairData.put("parentUid", storedParentUid);
                    pairData.put("childUid", childUid);
                    pairData.put("pairedAt", System.currentTimeMillis());
                    
                    firestore.collection("device_pairs")
                            .document(pairId)
                            .set(pairData)
                            .addOnSuccessListener(aVoid -> {
                                android.util.Log.d("QRPairingService", "Device pair created successfully");
                                
                                // Delete pairing token after successful pairing
                                firestore.collection("pairing_tokens")
                                        .document(qrData.getPairToken())
                                        .delete()
                                        .addOnSuccessListener(aVoid1 -> {
                                            android.util.Log.d("QRPairingService", "Pairing completed successfully");
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            android.util.Log.w("QRPairingService", "Failed to delete token: " + e.getMessage());
                                            // Still call success since pairing worked
                                            callback.onSuccess();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("QRPairingService", "Failed to create device pair: " + e.getMessage(), e);
                                callback.onFailure(new Exception("Failed to create device pair: " + e.getMessage()));
                            });
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("QRPairingService", "Failed to read pairing token: " + e.getMessage(), e);
                    callback.onFailure(new Exception("Failed to verify pairing token: " + e.getMessage()));
                });
    }

    /**
     * Clean up expired pairing tokens (can be called periodically)
     */
    public void cleanupExpiredTokens(CleanupCallback callback) {
        long now = System.currentTimeMillis();
        firestore.collection("pairing_tokens")
                .whereLessThan("expiresAt", now)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    // Delete all expired tokens
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }
                    callback.onSuccess(count);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Callback interfaces
    public interface QRPairingCallback {
        void onSuccess(QRPairingData pairingData);
        void onFailure(Exception exception);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    public interface CleanupCallback {
        void onSuccess(int deletedCount);
        void onFailure(Exception exception);
    }
}

