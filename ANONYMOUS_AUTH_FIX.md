# 🔴 URGENT: Enable Anonymous Authentication

## Error Message:
"Pairing failed: Failed to create child account: This operation is restricted to..."

## Problem:
Anonymous Authentication is **NOT enabled** in your Firebase Console. Child devices use anonymous authentication to automatically create accounts when scanning QR codes.

## ✅ Quick Fix (2 minutes):

### Step 1: Go to Firebase Console
Direct link: https://console.firebase.google.com/project/guardian-ai-edfa6/authentication/providers

### Step 2: Enable Anonymous Authentication

1. In Firebase Console, click **Authentication** in the left menu
2. Click on the **Sign-in method** tab
3. Scroll down and find **Anonymous** in the list
4. Click on **Anonymous**
5. **Enable** the toggle switch
6. Click **Save**

### Step 3: Test Again

1. Generate a QR code on the parent device
2. Scan it on the child device
3. The pairing should now work!

## Why This Is Needed:

- **Parent devices** use Email/Password authentication (already enabled)
- **Child devices** use Anonymous authentication (needs to be enabled)
- When a child scans the QR code, the app automatically:
  1. Creates an anonymous Firebase account
  2. Links it to the parent account
  3. Stores the pairing in Firestore

## Visual Guide:

```
Firebase Console → Authentication → Sign-in method → Anonymous → Enable → Save
```

## Still Not Working?

If you still get errors after enabling Anonymous Authentication:

1. **Check Firestore Security Rules:**
   - Go to: https://console.firebase.google.com/project/guardian-ai-edfa6/firestore/rules
   - Make sure rules allow anonymous users to create documents (see FIRESTORE_SECURITY_RULES.md)

2. **Check Logcat:**
   - Filter by "QRPairingService" or "QRScanActivity"
   - Look for specific error messages

3. **Verify Both Auth Methods Are Enabled:**
   - Email/Password: ✅ Enabled
   - Anonymous: ✅ Enabled

## Direct Links:

- **Authentication Settings:** https://console.firebase.google.com/project/guardian-ai-edfa6/authentication/providers
- **Firestore Rules:** https://console.firebase.google.com/project/guardian-ai-edfa6/firestore/rules









