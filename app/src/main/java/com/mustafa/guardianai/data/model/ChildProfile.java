package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Child Profile Model
 * Extended profile information for child users
 * Stored in Firestore 'child_profiles' collection
 */
public class ChildProfile {
    private String profileId;
    private String childUid;
    private String parentUid;
    private String name;
    private int age;
    private String deviceName;
    private String deviceType; // "iOS" or "Android"
    private String profilePictureUrl;
    private boolean isOnline;
    private long lastSeen;
    private String currentLocation;
    private long screenTimeLimit; // in seconds
    private long screenTimeToday; // in seconds
    private int screenTimePercentage; // calculated percentage
    private long createdAt;
    private long updatedAt;

    // Default constructor required for Firestore
    public ChildProfile() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isOnline = false;
        this.screenTimePercentage = 0;
    }

    public ChildProfile(String profileId, String childUid, String parentUid, String name, int age) {
        this.profileId = profileId;
        this.childUid = childUid;
        this.parentUid = parentUid;
        this.name = name;
        this.age = age;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isOnline = false;
        this.screenTimePercentage = 0;
        this.screenTimeLimit = 7200; // Default 2 hours
    }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("profileId", profileId);
        map.put("childUid", childUid);
        map.put("parentUid", parentUid);
        map.put("name", name);
        map.put("age", age);
        map.put("deviceName", deviceName);
        map.put("deviceType", deviceType);
        map.put("profilePictureUrl", profilePictureUrl);
        map.put("isOnline", isOnline);
        map.put("lastSeen", lastSeen);
        map.put("currentLocation", currentLocation);
        map.put("screenTimeLimit", screenTimeLimit);
        map.put("screenTimeToday", screenTimeToday);
        map.put("screenTimePercentage", screenTimePercentage);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    // Calculate screen time percentage
    public void calculateScreenTimePercentage() {
        if (screenTimeLimit > 0) {
            this.screenTimePercentage = (int) ((screenTimeToday * 100) / screenTimeLimit);
        } else {
            this.screenTimePercentage = 0;
        }
    }

    // Format screen time to readable string (e.g., "1h 45m")
    public String getFormattedScreenTime() {
        long hours = screenTimeToday / 3600;
        long minutes = (screenTimeToday % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    // Format screen time limit to readable string
    public String getFormattedScreenTimeLimit() {
        long hours = screenTimeLimit / 3600;
        long minutes = (screenTimeLimit % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    // Getters and Setters
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getChildUid() {
        return childUid;
    }

    public void setChildUid(String childUid) {
        this.childUid = childUid;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public long getScreenTimeLimit() {
        return screenTimeLimit;
    }

    public void setScreenTimeLimit(long screenTimeLimit) {
        this.screenTimeLimit = screenTimeLimit;
        calculateScreenTimePercentage();
    }

    public long getScreenTimeToday() {
        return screenTimeToday;
    }

    public void setScreenTimeToday(long screenTimeToday) {
        this.screenTimeToday = screenTimeToday;
        calculateScreenTimePercentage();
    }

    public int getScreenTimePercentage() {
        return screenTimePercentage;
    }

    public void setScreenTimePercentage(int screenTimePercentage) {
        this.screenTimePercentage = screenTimePercentage;
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









