# Firebase Setup Guide

## Important: Firebase Configuration Required

This project requires Firebase to be set up before it can run. Follow these steps:

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select an existing project
3. Follow the setup wizard

## Step 2: Add Android Apps to Firebase

You need to add **TWO separate Android apps** to your Firebase project:

### Child App
1. In Firebase Console, click "Add app" → Android
2. Register app with:
   - **Package name**: `com.mustafa.guardianai.child`
   - **App nickname**: Guardian AI - Child (optional)
   - **Debug signing certificate SHA-1**: (optional for FYP, but recommended)
3. Download `google-services.json`
4. Place it in: `child-app/google-services.json`

### Parent App
1. In Firebase Console, click "Add app" → Android again
2. Register app with:
   - **Package name**: `com.mustafa.guardianai.parent`
   - **App nickname**: Guardian AI - Parent (optional)
   - **Debug signing certificate SHA-1**: (optional for FYP, but recommended)
3. Download `google-services.json`
4. Place it in: `parent-app/google-services.json`

## Step 3: Enable Firebase Services

### Authentication
1. Go to Firebase Console → Authentication
2. Click "Get started"
3. Enable **Email/Password** sign-in method

### Firestore Database
1. Go to Firebase Console → Firestore Database
2. Click "Create database"
3. Start in **test mode** (for FYP development)
4. Choose a location (closest to your region)

### Cloud Messaging (FCM)
1. Go to Firebase Console → Cloud Messaging
2. FCM is automatically enabled when you add Android apps

## Step 4: Firestore Security Rules (For FYP)

For development/testing, you can use these basic rules. **Note: These are NOT production-ready!**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own user document
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Device pairs - parents can read their pairs, children can read their own
    match /device_pairs/{pairId} {
      allow read: if request.auth != null && 
        (resource.data.parentUid == request.auth.uid || 
         resource.data.childUid == request.auth.uid);
      allow write: if request.auth != null && 
        request.auth.uid == resource.data.parentUid;
    }
    
    // Safety rules - children can read their rules, parents can write
    match /safety_rules/{ruleId} {
      allow read: if request.auth != null && 
        (resource.data.childUid == request.auth.uid || 
         resource.data.parentUid == request.auth.uid);
      allow write: if request.auth != null && 
        request.auth.uid == resource.data.parentUid;
    }
    
    // Alerts - parents can read their alerts, children can create alerts
    match /alerts/{alertId} {
      allow read: if request.auth != null && 
        resource.data.parentUid == request.auth.uid;
      allow create: if request.auth != null && 
        request.auth.uid == request.resource.data.childUid;
      allow update: if request.auth != null && 
        request.auth.uid == resource.data.parentUid;
    }
  }
}
```

## Step 5: Verify Setup

After adding `google-services.json` files:
1. Sync Gradle files in Android Studio
2. Build the project
3. Check for any Firebase-related errors

## Troubleshooting

### "google-services.json not found"
- Make sure you downloaded the correct `google-services.json` for each app
- Verify the file is in the correct location:
  - `child-app/google-services.json`
  - `parent-app/google-services.json`

### "Package name mismatch"
- Verify the package name in `google-services.json` matches:
  - Child: `com.mustafa.guardianai.child`
  - Parent: `com.mustafa.guardianai.parent`

### Build errors
- Clean and rebuild the project
- Invalidate caches: File → Invalidate Caches / Restart

## Note for FYP

- Test mode Firestore rules are fine for development
- You can document security considerations in your FYP report
- For demonstration, you can show the security rules you would use in production


