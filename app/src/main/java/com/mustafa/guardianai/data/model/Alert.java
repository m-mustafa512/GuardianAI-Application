package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Alert Model
 * Represents notifications/alerts for parents
 * Stored in Firestore 'alerts' collection
 */
public class Alert {
    public enum AlertType {
        GEO_FENCE_BREACH,
        TIME_LIMIT_REACHED,
        NEW_APP_INSTALL,
        LOW_BATTERY,
        WEEKLY_REPORT,
        UNKNOWN
    }

    public enum AlertSeverity {
        HIGH,
        MEDIUM,
        LOW
    }

    private String alertId;
    private String parentUid;
    private String childUid;
    private AlertType type;
    private String title;
    private String message;
    private AlertSeverity severity;
    private boolean isRead;
    private boolean isResolved;
    private long createdAt;
    private long resolvedAt;

    // Default constructor required for Firestore
    public Alert() {
        this.createdAt = System.currentTimeMillis();
        this.isRead = false;
        this.isResolved = false;
    }

    public Alert(String alertId, String parentUid, String childUid, AlertType type, 
                 String title, String message, AlertSeverity severity) {
        this.alertId = alertId;
        this.parentUid = parentUid;
        this.childUid = childUid;
        this.type = type;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.createdAt = System.currentTimeMillis();
        this.isRead = false;
        this.isResolved = false;
    }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("alertId", alertId);
        map.put("parentUid", parentUid);
        map.put("childUid", childUid);
        map.put("type", type != null ? type.name() : AlertType.UNKNOWN.name());
        map.put("title", title);
        map.put("message", message);
        map.put("severity", severity != null ? severity.name() : AlertSeverity.MEDIUM.name());
        map.put("isRead", isRead);
        map.put("isResolved", isResolved);
        map.put("createdAt", createdAt);
        map.put("resolvedAt", resolvedAt);
        return map;
    }

    // Parse AlertType from string (for Firestore)
    public static AlertType parseType(String typeString) {
        try {
            return AlertType.valueOf(typeString);
        } catch (Exception e) {
            return AlertType.UNKNOWN;
        }
    }

    // Parse AlertSeverity from string (for Firestore)
    public static AlertSeverity parseSeverity(String severityString) {
        try {
            return AlertSeverity.valueOf(severityString);
        } catch (Exception e) {
            return AlertSeverity.MEDIUM;
        }
    }

    // Format time ago (e.g., "10m ago", "1h ago")
    public String getTimeAgo() {
        long now = System.currentTimeMillis();
        long diff = now - createdAt;
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
    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getChildUid() {
        return childUid;
    }

    public void setChildUid(String childUid) {
        this.childUid = childUid;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
        if (resolved && resolvedAt == 0) {
            resolvedAt = System.currentTimeMillis();
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(long resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}

