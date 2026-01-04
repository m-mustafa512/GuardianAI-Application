package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Summary Model
 * Aggregated data for parent dashboard overview
 * Stored in Firestore 'dashboard_summaries' collection
 */
public class DashboardSummary {
    private String summaryId; // Same as parentUid
    private String parentUid;
    private int totalDevices;
    private int totalAlerts;
    private int unreadAlerts;
    private long totalScreenTime; // in seconds
    private long lastUpdated;

    // Default constructor required for Firestore
    public DashboardSummary() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public DashboardSummary(String parentUid) {
        this.summaryId = parentUid;
        this.parentUid = parentUid;
        this.totalDevices = 0;
        this.totalAlerts = 0;
        this.unreadAlerts = 0;
        this.totalScreenTime = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("summaryId", summaryId);
        map.put("parentUid", parentUid);
        map.put("totalDevices", totalDevices);
        map.put("totalAlerts", totalAlerts);
        map.put("unreadAlerts", unreadAlerts);
        map.put("totalScreenTime", totalScreenTime);
        map.put("lastUpdated", lastUpdated);
        return map;
    }

    // Format total screen time to readable string (e.g., "4h")
    public String getFormattedTotalScreenTime() {
        long hours = totalScreenTime / 3600;
        long minutes = (totalScreenTime % 3600) / 60;
        if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return "0m";
        }
    }

    // Getters and Setters
    public String getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public int getTotalDevices() {
        return totalDevices;
    }

    public void setTotalDevices(int totalDevices) {
        this.totalDevices = totalDevices;
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public void setTotalAlerts(int totalAlerts) {
        this.totalAlerts = totalAlerts;
    }

    public int getUnreadAlerts() {
        return unreadAlerts;
    }

    public void setUnreadAlerts(int unreadAlerts) {
        this.unreadAlerts = unreadAlerts;
    }

    public long getTotalScreenTime() {
        return totalScreenTime;
    }

    public void setTotalScreenTime(long totalScreenTime) {
        this.totalScreenTime = totalScreenTime;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}









