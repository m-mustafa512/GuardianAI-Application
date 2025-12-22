# Firestore Security Rules for Guardian AI

## ⚠️ IMPORTANT: Copy these rules to Firebase Console

The QR code pairing feature requires specific Firestore security rules. Follow these steps:

## Step-by-Step Setup:

1. **Go to Firebase Console:**
   - https://console.firebase.google.com/project/guardian-ai-edfa6/firestore/rules

2. **Replace the existing rules with the following:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow anyone to READ pairing tokens (for QR validation during child pairing)
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

3. **Click "Publish" to save the rules**

## Why These Rules?

### pairing_tokens Collection:
- **Read: `if true`** - Allows unauthenticated reads because:
  - Child device needs to validate the QR code token BEFORE authentication
  - The QR scan happens before anonymous authentication
  - This is safe because tokens are temporary and expire in 10 minutes
  
- **Write/Delete: `if request.auth != null`** - Only authenticated users (parents) can create/delete tokens

### users Collection:
- **Read**: Authenticated users can read user data
- **Create/Update**: Users can only create/update their own document (by matching UID)
- **Delete**: Disabled for safety

### device_pairs Collection:
- **Read/Write**: Only authenticated users can access device pairs

## Security Notes:

⚠️ **For Development/Testing Only:**
These rules are designed for development and testing. For production, you should:
1. Add more restrictive rules
2. Add validation for data structure
3. Consider adding rate limiting
4. Add logging for security events

## Testing:

After updating the rules:
1. Try generating a QR code on the parent device
2. Try scanning the QR code on the child device
3. Check Logcat for any permission errors

## Direct Link to Rules:

https://console.firebase.google.com/project/guardian-ai-edfa6/firestore/rules

