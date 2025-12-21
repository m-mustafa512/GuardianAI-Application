package com.mustafa.guardianai.data.model;

/**
 * User data model
 * Represents a user in the system (parent or child)
 */
public class User {
    private String uid;
    private String email;
    private String displayName;
    private UserRole role;
    private String parentUid; // For child users, links to parent
    private String deviceId;
    private boolean emailVerified;
    private long createdAt;

    // Default constructor required for Firestore
    public User() {
        this.createdAt = System.currentTimeMillis();
    }

    public User(String uid, String email, String displayName, UserRole role, 
                String parentUid, String deviceId, boolean emailVerified) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.parentUid = parentUid;
        this.deviceId = deviceId;
        this.emailVerified = emailVerified;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}


