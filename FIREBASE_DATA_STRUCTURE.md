# Firebase Data Structure for Parent Dashboard Module

## Collections Overview

### 1. `users` Collection
**Purpose**: Store user profiles (both parent and child)

**Document Structure**:
```json
{
  "uid": "firebase_auth_uid",
  "email": "user@example.com",
  "displayName": "User Name",
  "role": "PARENT" | "CHILD",
  "parentUid": "parent_uid_if_child",
  "deviceId": "android_device_id",
  "emailVerified": true/false,
  "createdAt": timestamp
}
```

### 2. `child_profiles` Collection
**Purpose**: Extended child profile information with additional metadata

**Document Structure**:
```json
{
  "profileId": "unique_profile_id",
  "childUid": "firebase_auth_uid_of_child",
  "parentUid": "firebase_auth_uid_of_parent",
  "name": "Child Name",
  "age": 10,
  "deviceName": "iPhone 13",
  "deviceType": "iOS" | "Android",
  "profilePictureUrl": "url_or_null",
  "isOnline": true/false,
  "lastSeen": timestamp,
  "currentLocation": "Bedroom",
  "screenTimeLimit": 7200, // seconds
  "screenTimeToday": 3600, // seconds
  "screenTimePercentage": 50, // percentage
  "createdAt": timestamp,
  "updatedAt": timestamp
}
```

### 3. `alerts` Collection
**Purpose**: Store alerts/notifications for parents

**Document Structure**:
```json
{
  "alertId": "unique_alert_id",
  "parentUid": "firebase_auth_uid_of_parent",
  "childUid": "firebase_auth_uid_of_child",
  "type": "GEO_FENCE_BREACH" | "TIME_LIMIT_REACHED" | "NEW_APP_INSTALL" | "LOW_BATTERY" | "WEEKLY_REPORT",
  "title": "Alert Title",
  "message": "Alert description",
  "severity": "HIGH" | "MEDIUM" | "LOW",
  "isRead": true/false,
  "isResolved": true/false,
  "createdAt": timestamp,
  "resolvedAt": timestamp
}
```

### 4. `dashboard_summaries` Collection
**Purpose**: Store aggregated dashboard data for quick access

**Document Structure**:
```json
{
  "summaryId": "parent_uid", // One summary per parent
  "parentUid": "firebase_auth_uid_of_parent",
  "totalDevices": 3,
  "totalAlerts": 1,
  "unreadAlerts": 1,
  "totalScreenTime": 14400, // seconds
  "lastUpdated": timestamp
}
```

### 5. `device_pairs` Collection (Already exists)
**Purpose**: Track parent-child device pairings

**Document Structure**:
```json
{
  "pairId": "unique_pair_id",
  "parentUid": "firebase_auth_uid_of_parent",
  "childUid": "firebase_auth_uid_of_child",
  "parentDeviceId": "parent_device_id",
  "childDeviceId": "child_device_id",
  "pairedAt": timestamp,
  "isActive": true/false
}
```

## Data Flow

### From Child Device to Parent Dashboard:
1. **Child device** collects data (app usage, location, etc.)
2. **Child device** writes to Firestore:
   - Updates `child_profiles` collection with current status
   - Creates entries in `alerts` collection when events occur
3. **Parent dashboard** listens to Firestore:
   - Real-time listener on `child_profiles` where `parentUid == current_parent_uid`
   - Real-time listener on `alerts` where `parentUid == current_parent_uid`
   - Real-time listener on `dashboard_summaries` for aggregated data

### Parent Dashboard Operations:
1. **View Child Profiles**: Query `child_profiles` where `parentUid == current_parent_uid`
2. **Create Child Profile**: Create document in `child_profiles` (manual creation)
3. **Delete Child Profile**: Delete document from `child_profiles` and related `device_pairs`
4. **View Alerts**: Query `alerts` where `parentUid == current_parent_uid` and `isRead == false`
5. **Update Profile**: Update `users` collection for parent profile

## Security Rules (Firestore)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Child profiles - parents can read/write their own children's profiles
    match /child_profiles/{profileId} {
      allow read, write: if request.auth != null && 
        (resource.data.parentUid == request.auth.uid || 
         request.resource.data.parentUid == request.auth.uid);
    }
    
    // Alerts - parents can read/write alerts for their children
    match /alerts/{alertId} {
      allow read, write: if request.auth != null && 
        (resource.data.parentUid == request.auth.uid || 
         request.resource.data.parentUid == request.auth.uid);
    }
    
    // Dashboard summaries - parents can read/write their own summary
    match /dashboard_summaries/{summaryId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == summaryId;
    }
    
    // Users - already defined in previous rules
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && request.auth.uid == userId;
      allow delete: if false;
    }
  }
}
```









