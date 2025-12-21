# Firebase Setup Checklist - URGENT

## ⚠️ CRITICAL: Your app is showing `[CONFIGURATION_NOT_FOUND]` error

This error means **Firebase Authentication is NOT enabled** in your Firebase Console.

## 🔧 IMMEDIATE FIX REQUIRED:

### Step 1: Enable Email/Password Authentication

1. Go to Firebase Console: https://console.firebase.google.com/
2. Select your project: **guardian-ai-edfa6**
3. Click on **Authentication** in the left menu
4. If you see "Get started", click it
5. Click on the **Sign-in method** tab
6. Click on **Email/Password**
7. **Enable** the first toggle (Email/Password)
8. Click **Save**

### Step 2: Create Firestore Database (if not created)

1. In Firebase Console, click on **Firestore Database**
2. Click **Create database**
3. Select **Start in test mode** (for development)
4. Choose a location (closest to your region)
5. Click **Enable**

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


