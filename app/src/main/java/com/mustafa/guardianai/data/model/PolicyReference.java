package com.mustafa.guardianai.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Policy Reference Model
 * References to policies/rules without containing the actual policy logic
 * Used for linking policies to child profiles and tracking policy status
 * 
 * Note: This is a reference model only. Actual policy logic will be implemented
 * in modules 4 (App Usage & Screen Time) and 5 (App Access Control & Blocking)
 */
public class PolicyReference {
    private String policyId;
    private String parentUid;
    private String childUid;
    private String policyType; // "SCREEN_TIME", "APP_BLOCKING", "LOCATION", etc.
    private boolean isActive;
    private long createdAt;
    private long updatedAt;
    private long lastAppliedAt; // When policy was last applied/enforced

    // Default constructor required for Firestore
    public PolicyReference() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isActive = true;
    }

    public PolicyReference(String policyId, String parentUid, String childUid, String policyType) {
        this.policyId = policyId;
        this.parentUid = parentUid;
        this.childUid = childUid;
        this.policyType = policyType;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("policyId", policyId);
        map.put("parentUid", parentUid);
        map.put("childUid", childUid);
        map.put("policyType", policyType);
        map.put("isActive", isActive);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        map.put("lastAppliedAt", lastAppliedAt);
        return map;
    }

    // Getters and Setters
    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
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

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public long getLastAppliedAt() {
        return lastAppliedAt;
    }

    public void setLastAppliedAt(long lastAppliedAt) {
        this.lastAppliedAt = lastAppliedAt;
    }
}








