package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Child Summary Model
 * High-level summary data for child profiles (used in dashboard)
 * Contains aggregated information without detailed app-level data
 * 
 * This is a lightweight model for displaying child overview in parent dashboard
 */
public class ChildSummary {
    private String childUid;
    private String childName;
    private String deviceName;
    private boolean isOnline;
    private long lastSeen;
    private long totalScreenTimeMillis; // Total screen time today in milliseconds
    private int alertsCount; // Number of alerts for this child
    private String deviceStatus; // "Online", "Offline", "Charging", etc.
    private String profilePictureUrl;

    // Default constructor
    public ChildSummary() {
        this.isOnline = false;
        this.totalScreenTimeMillis = 0;
        this.alertsCount = 0;
        this.deviceStatus = "Offline";
    }

    public ChildSummary(String childUid, String childName) {
        this.childUid = childUid;
        this.childName = childName;
        this.isOnline = false;
        this.totalScreenTimeMillis = 0;
        this.alertsCount = 0;
        this.deviceStatus = "Offline";
    }

    // Convert to Firestore Map (if needed for caching)
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("childUid", childUid);
        map.put("childName", childName);
        map.put("deviceName", deviceName);
        map.put("isOnline", isOnline);
        map.put("lastSeen", lastSeen);
        map.put("totalScreenTimeMillis", totalScreenTimeMillis);
        map.put("alertsCount", alertsCount);
        map.put("deviceStatus", deviceStatus);
        map.put("profilePictureUrl", profilePictureUrl);
        return map;
    }

    // Format screen time to readable string (e.g., "2h 30m")
    public String getFormattedScreenTime() {
        long totalSeconds = totalScreenTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return "0m";
        }
    }

    // Format last seen time (e.g., "5m ago", "1h ago")
    public String getFormattedLastSeen() {
        if (isOnline) {
            return "Online";
        }
        long now = System.currentTimeMillis();
        long diff = now - lastSeen;
        long minutes = diff / (1000 * 60);
        long hours = diff / (1000 * 60 * 60);
        long days = diff / (1000 * 60 * 60 * 24);

        if (days > 0) {
            return days + "d ago";
        } else if (hours > 0) {
            return hours + "h ago";
        } else if (minutes > 0) {
            return minutes + "m ago";
        } else {
            return "Just now";
        }
    }

    // Getters and Setters
    public String getChildUid() {
        return childUid;
    }

    public void setChildUid(String childUid) {
        this.childUid = childUid;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public long getTotalScreenTimeMillis() {
        return totalScreenTimeMillis;
    }

    public void setTotalScreenTimeMillis(long totalScreenTimeMillis) {
        this.totalScreenTimeMillis = totalScreenTimeMillis;
    }

    public int getAlertsCount() {
        return alertsCount;
    }

    public void setAlertsCount(int alertsCount) {
        this.alertsCount = alertsCount;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}








