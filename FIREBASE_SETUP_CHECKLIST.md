# Firebase Setup Checklist - URGENT

## ⚠️ CRITICAL: Your app is showing `[CONFIGURATION_NOT_FOUND]` error

This error means **Firebase Authentication is NOT enabled** in your Firebase Console.

## 🔧 IMMEDIATE FIX REQUIRED:

### Step 1: Enable Authentication Methods

**⚠️ CRITICAL: Both methods are required for the app to work!**

1. Go to Firebase Console: https://console.firebase.google.com/
2. Select your project: **guardian-ai-edfa6**
3. Click on **Authentication** in the left menu
4. If you see "Get started", click it
5. Click on the **Sign-in method** tab

#### Enable Email/Password (for Parent Login):
6. Click on **Email/Password**
7. **Enable** the first toggle (Email/Password)
8. Click **Save**

#### Enable Anonymous Authentication (for Child QR Pairing):
**⚠️ THIS IS REQUIRED FOR CHILD DEVICE PAIRING!**
9. Click on **Anonymous** in the list of sign-in providers
10. **Enable** the toggle
11. Click **Save**

**Why Anonymous Auth?** Child devices don't have email/password. They use anonymous authentication when scanning the QR code to automatically create their account.

### Step 2: Create Firestore Database (if not created)

1. In Firebase Console, click on **Firestore Database**
2. Click **Create database**
3. Select **Start in test mode** (for development)
4. Choose a location (closest to your region)
5. Click **Enable**

**⚠️ IMPORTANT for QR Code Generation:**
After creating the database, you need to set up security rules to allow read/write access:

1. In Firestore Database, click on the **Rules** tab
2. Replace the rules with the following (for development/testing):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow anyone to READ pairing tokens (for QR validation)
    // But only authenticated users can WRITE (create) them
    match /pairing_tokens/{tokenId} {
      allow read: if true;  // Allow unauthenticated reads for QR validation
      allow write: if request.auth != null;  // Only authenticated users can create tokens
      allow delete: if request.auth != null;  // Only authenticated users can delete tokens
    }
    // Allow users to read/write their own user data
    // Also allow anonymous users to create their own user document
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && request.auth.uid == userId;
      allow delete: if false;  // Prevent deletion for safety
    }
    // Allow authenticated users to read/write device pairs
    match /device_pairs/{pairId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```
3. Click **Publish** to save the rules

**Note:** These are test rules. For production, you should implement more restrictive security rules.

### Step 3: Verify google-services.json

Your `google-services.json` file is located at: `app/google-services.json`

Make sure it contains:
- Project ID: `guardian-ai-edfa6`
- Package name: `com.mustafa.guardianai`

### Step 4: Clean and Rebuild

After enabling Authentication:
1. In Android Studio: **Build** → **Clean Project**
2. Then: **Build** → **Rebuild Project**
3. Run the app again

## ✅ Verification

After completing the above steps, try signing up again. The error should be resolved.

## 🔗 Direct Links

- Firebase Console: https://console.firebase.google.com/project/guardian-ai-edfa6
- Authentication Settings: https://console.firebase.google.com/project/guardian-ai-edfa6/authentication/providers
- Firestore Database: https://console.firebase.google.com/project/guardian-ai-edfa6/firestore

## 📝 Common Issues

### "CONFIGURATION_NOT_FOUND"
- **Cause**: Email/Password authentication not enabled
- **Fix**: Enable it in Firebase Console → Authentication → Sign-in method

### "Network error"
- **Cause**: No internet connection or Firebase project issues
- **Fix**: Check internet connection and verify Firebase project is active

### "Invalid API key"
- **Cause**: google-services.json is incorrect or outdated
- **Fix**: Re-download google-services.json from Firebase Console

### "QR Code not showing / Loading forever"
- **Cause**: Firestore Database not set up or security rules blocking access
- **Fix**: 
  1. Create Firestore Database (Step 2 above)
  2. Set up security rules (see Step 2 for rules)
  3. Check Logcat for error messages (look for "ParentDashboard" tag)
  4. Ensure you're logged in as a parent user


