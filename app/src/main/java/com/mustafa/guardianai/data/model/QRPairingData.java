package com.mustafa.guardianai.data.model;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code pairing data model
 * Contains information for secure device pairing
 */
public class QRPairingData {
    private String parentUid;
    private String parentEmail;
    private String pairToken; // Unique token for this pairing session
    private long expiresAt; // Expiration timestamp
    private long timestamp;

    // Default constructor required for Firestore
    public QRPairingData() {
        this.timestamp = System.currentTimeMillis();
    }

    public QRPairingData(String parentUid, String parentEmail, String pairToken, long expiresAt) {
        this.parentUid = parentUid;
        this.parentEmail = parentEmail;
        this.pairToken = pairToken;
        this.expiresAt = expiresAt;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Convert to Map for Firestore storage
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("parentUid", parentUid != null ? parentUid : "");
        map.put("parentEmail", parentEmail != null ? parentEmail : "");
        map.put("pairToken", pairToken != null ? pairToken : "");
        map.put("expiresAt", expiresAt);
        map.put("timestamp", timestamp);
        return map;
    }

    /**
     * Convert to JSON string for QR code encoding
     * Uses Android's built-in JSONObject (no external dependency)
     * QR code contains ONLY pairToken - parentUid is trusted from Firestore
     */
    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("pairToken", pairToken != null ? pairToken : "");
            return json.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

    /**
     * Parse JSON string from QR code
     * Uses Android's built-in JSONObject (no external dependency)
     * QR code contains ONLY pairToken - parentUid is read from Firestore
     */
    public static QRPairingData fromJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                android.util.Log.e("QRPairingData", "JSON string is null or empty");
                return null;
            }
            
            JSONObject jsonObject = new JSONObject(json);
            QRPairingData data = new QRPairingData();
            
            String pairToken = jsonObject.optString("pairToken", "");
            
            // Validate required field - only pairToken is required
            if (pairToken.isEmpty()) {
                android.util.Log.e("QRPairingData", "Missing required field - pairToken: " + pairToken);
                return null;
            }
            
            data.setPairToken(pairToken);
            
            android.util.Log.d("QRPairingData", "Successfully parsed QR data - Token: " + pairToken);
            return data;
        } catch (JSONException e) {
            android.util.Log.e("QRPairingData", "JSON parsing error: " + e.getMessage(), e);
            return null;
        }
    }

    // Getters and Setters
    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getPairToken() {
        return pairToken;
    }

    public void setPairToken(String pairToken) {
        this.pairToken = pairToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

