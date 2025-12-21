package com.mustafa.guardianai.network;

import android.content.Context;
import android.provider.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mustafa.guardianai.data.model.QRPairingData;
import com.mustafa.guardianai.data.model.User;
import com.mustafa.guardianai.data.model.UserRole;

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
    private static final long PAIRING_EXPIRY_TIME = 10 * 60 * 1000L; // 10 minutes

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
        firestore.collection("pairing_tokens")
                .document(pairToken)
                .set(pairingData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(pairingData))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Validate and process QR pairing data from child device
     * @param qrData QR pairing data scanned by child
     * @param callback Callback for result
     */
    public void processPairing(QRPairingData qrData, SimpleCallback callback) {
        // Validate expiration
        if (System.currentTimeMillis() > qrData.getExpiresAt()) {
            callback.onFailure(new Exception("Pairing token has expired"));
            return;
        }

        // Verify token exists in Firestore
        firestore.collection("pairing_tokens")
                .document(qrData.getPairToken())
                .get()
                .addOnSuccessListener(tokenDoc -> {
                    if (!tokenDoc.exists()) {
                        callback.onFailure(new Exception("Invalid pairing token"));
                        return;
                    }

                    QRPairingData storedData = tokenDoc.toObject(QRPairingData.class);
                    if (storedData == null) {
                        callback.onFailure(new Exception("Invalid pairing data"));
                        return;
                    }

                    // Verify parent UID matches
                    if (!storedData.getParentUid().equals(qrData.getParentUid())) {
                        callback.onFailure(new Exception("Pairing token mismatch"));
                        return;
                    }

                    FirebaseUser currentUser = auth.getCurrentUser();
                    if (currentUser == null) {
                        callback.onFailure(new Exception("No user logged in"));
                        return;
                    }

                    // Get device ID
                    Context context = auth.getApp().getApplicationContext();
                    String childDeviceId = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    );

                    // Create child user document in Firestore
                    User childUser = new User(
                            currentUser.getUid(),
                            currentUser.getEmail() != null ? currentUser.getEmail() : "",
                            currentUser.getDisplayName(),
                            UserRole.CHILD,
                            qrData.getParentUid(),
                            childDeviceId,
                            currentUser.isEmailVerified()
                    );

                    firestore.collection("users")
                            .document(currentUser.getUid())
                            .set(childUser)
                            .addOnSuccessListener(aVoid -> {
                                // Create device pair document
                                String pairId = UUID.randomUUID().toString();
                                Map<String, Object> pairData = new HashMap<>();
                                pairData.put("pairId", pairId);
                                pairData.put("parentUid", qrData.getParentUid());
                                pairData.put("childUid", currentUser.getUid());
                                pairData.put("parentDeviceId", storedData.getParentUid());
                                pairData.put("childDeviceId", childDeviceId);
                                pairData.put("pairedAt", System.currentTimeMillis());
                                pairData.put("isActive", true);

                                firestore.collection("device_pairs")
                                        .document(pairId)
                                        .set(pairData)
                                        .addOnSuccessListener(aVoid1 -> {
                                            // Delete used pairing token
                                            firestore.collection("pairing_tokens")
                                                    .document(qrData.getPairToken())
                                                    .delete()
                                                    .addOnSuccessListener(aVoid2 -> 
                                                            callback.onSuccess())
                                                    .addOnFailureListener(callback::onFailure);
                                        })
                                        .addOnFailureListener(callback::onFailure);
                            })
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
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

