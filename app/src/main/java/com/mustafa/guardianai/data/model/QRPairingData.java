package com.mustafa.guardianai.data.model;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * QR Code pairing data model
 * Contains information for secure device pairing
 * Note: QR feature is not yet implemented - this is a placeholder
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
     * Convert to JSON string for QR code encoding
     * Uses Android's built-in JSONObject (no external dependency)
     */
//    public String toJson() {
//        try {
//            JSONObject json = new JSONObject();
//            json.put("parentUid", parentUid != null ? parentUid : "");
//            json.put("parentEmail", parentEmail != null ? parentEmail : "");
//            json.put("pairToken", pairToken != null ? pairToken : "");
//            json.put("expiresAt", expiresAt);
//            json.put("timestamp", timestamp);
//            return json.toString();
//        } catch (JSONException e) {
//            return "{}";
//        }
//    }
    public String toJson() {
        // Encode ONLY the pairing token to keep QR small and decodable
        return "PAIR:" + (pairToken != null ? pairToken : "");
    }


    /**
     * Parse JSON string from QR code
     * Uses Android's built-in JSONObject (no external dependency)
     */
//    public static QRPairingData fromJson(String json) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            QRPairingData data = new QRPairingData();
//            data.setParentUid(jsonObject.optString("parentUid", ""));
//            data.setParentEmail(jsonObject.optString("parentEmail", ""));
//            data.setPairToken(jsonObject.optString("pairToken", ""));
//            data.setExpiresAt(jsonObject.optLong("expiresAt", 0));
//            data.setTimestamp(jsonObject.optLong("timestamp", System.currentTimeMillis()));
//            return data;
//        } catch (JSONException e) {
//            return null;
//        }
//    }
    public static QRPairingData fromJson(String qrText) {
        if (qrText == null) return null;

        if (qrText.startsWith("PAIR:")) {
            QRPairingData data = new QRPairingData();
            data.setPairToken(qrText.substring(5));
            return data;
        }

        return null;
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

