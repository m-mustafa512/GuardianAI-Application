package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Parent Profile Model
 * Extended profile information for parent users
 * Stored in Firestore 'parent_profiles' collection
 */
public class ParentProfile {
    private String profileId; // Same as parentUid
    private String parentUid;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String phoneNumber;
    private int totalChildren;
    private long createdAt;
    private long updatedAt;

    // Default constructor required for Firestore
    public ParentProfile() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.totalChildren = 0;
    }

    public ParentProfile(String parentUid, String name, String email) {
        this.profileId = parentUid;
        this.parentUid = parentUid;
        this.name = name;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.totalChildren = 0;
    }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("profileId", profileId);
        map.put("parentUid", parentUid);
        map.put("name", name);
        map.put("email", email);
        map.put("profilePictureUrl", profilePictureUrl);
        map.put("phoneNumber", phoneNumber);
        map.put("totalChildren", totalChildren);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    // Getters and Setters
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTotalChildren() {
        return totalChildren;
    }

    public void setTotalChildren(int totalChildren) {
        this.totalChildren = totalChildren;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}








