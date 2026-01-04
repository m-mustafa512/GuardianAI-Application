# Guardian AI - Shared Foundation Documentation

## Overview

This document describes the **Shared Foundation** layer for Guardian AI, which provides reusable infrastructure for modules 1-6. This foundation ensures consistency, maintainability, and scalability across all modules.

---

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer                              │
│  (Activities, Fragments, Custom Views)                   │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                 ViewModel Layer                           │
│  (BaseViewModel, Module-specific ViewModels)              │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              Repository Layer                             │
│  (BaseRepository, Module-specific Repositories)          │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│            Firebase Integration Layer                     │
│  (AuthService, FCMTokenService, Firestore)                │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                 Data Models                               │
│  (User, ChildProfile, ParentProfile, etc.)               │
└──────────────────────────────────────────────────────────┘
```

---

## Navigation Structure

### App Flow

```
SplashActivity
    ↓
RoleSelectionActivity
    ├─→ Parent Flow
    │     ├─→ LoginActivity
    │     │     ├─→ SignUpActivity → SignUpSuccessActivity
    │     │     └─→ ForgotPasswordActivity → ForgotPasswordSuccessActivity
    │     │
    │     └─→ ParentDashboardActivity (with fragments)
    │           ├─→ DashboardFragment (Home)
    │           ├─→ ReportsFragment
    │           └─→ SettingsFragment
    │
    └─→ Child Flow
          ├─→ ChildLoginInfoActivity
          │     └─→ QRScanActivity
          │           └─→ ChildDashboardActivity
```

### Navigation Principles

1. **Role-Based Separation**: Parent and Child flows are completely separate after role selection
2. **Fragment-Based Navigation**: Parent dashboard uses fragments for tab navigation
3. **Activity-Based Navigation**: Major flows (login, signup) use activities
4. **No Back Navigation**: After login, users cannot go back to login screen (use `finish()`)

---

## Package Structure

```
com.mustafa.guardianai/
├── ui/
│   ├── base/                    # Base classes
│   │   ├── BaseActivity.java
│   │   └── BaseFragment.java
│   ├── auth/                    # Module 1: Authentication
│   ├── parent/                  # Module 2: Parent Dashboard
│   └── child/                   # Module 3: Child Interface
│
├── data/
│   ├── model/                   # Data models
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── ParentProfile.java
│   │   ├── ChildProfile.java
│   │   ├── ChildSummary.java
│   │   ├── Alert.java
│   │   ├── DashboardSummary.java
│   │   └── PolicyReference.java
│   └── repository/              # Repository pattern
│       └── BaseRepository.java
│
├── network/                     # Firebase integration
│   ├── AuthService.java
│   ├── FCMTokenService.java
│   ├── ChildProfileService.java
│   ├── DashboardService.java
│   └── QRPairingService.java
│
├── ui/base/
│   └── BaseViewModel.java
│
└── utils/                       # Utility classes
    ├── EmailValidator.java
    ├── PasswordValidator.java
    ├── BiometricHelper.java
    └── QRCodeGenerator.java
```

---

## Base Classes

### BaseActivity

**Location**: `ui/base/BaseActivity.java`

**Purpose**: Provides common functionality for all activities

**Features**:
- Toast message helpers (`showToast()`, `showError()`)
- Loading state management (`showLoading()`)
- Common lifecycle handling

**Usage**:
```java
public class MyActivity extends BaseActivity {
    @Override
    protected void setupUI() {
        // Setup UI components
    }
}
```

### BaseFragment

**Location**: `ui/base/BaseFragment.java`

**Purpose**: Provides common functionality for all fragments

**Features**:
- Toast message helpers
- Fragment attachment checking (`isFragmentAttached()`)
- Common lifecycle handling

**Usage**:
```java
public class MyFragment extends BaseFragment {
    @Override
    protected void setupUI() {
        // Setup UI components
    }
}
```

### BaseViewModel

**Location**: `ui/base/BaseViewModel.java`

**Purpose**: Provides common functionality for all ViewModels

**Features**:
- Error handling (`handleError()`)
- Resource cleanup (`onCleared()`)

**Usage**:
```java
public class MyViewModel extends BaseViewModel {
    // ViewModel logic
}
```

### BaseRepository

**Location**: `data/repository/BaseRepository.java`

**Purpose**: Provides common Firebase Firestore access

**Features**:
- Firestore instance access
- Error handling

**Usage**:
```java
public class MyRepository extends BaseRepository {
    public void fetchData() {
        getFirestore().collection("my_collection")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Handle success
                } else {
                    handleError(task.getException());
                }
            });
    }
}
```

---

## Shared UI Components

### Theme & Styles

**Location**: `res/values/styles.xml`

All UI components use styles from `styles.xml` to maintain consistency with the Login Module theme.

#### Key Styles:

1. **Primary Button** (`Widget.GuardianAI.PrimaryButton`)
   - Blue background (#4A90E2)
   - White text, bold
   - 56dp height, 12dp corner radius

2. **Secondary Button** (`Widget.GuardianAI.SecondaryButton`)
   - Outlined style
   - Blue border and text
   - Same dimensions as primary

3. **Card Container** (`Widget.GuardianAI.CardContainer`)
   - White background
   - 12dp corner radius
   - 2dp elevation

4. **Text Input** (`Widget.GuardianAI.TextInputLayout`)
   - White filled background
   - 12dp corner radius
   - Matches Login Module style

5. **Text Styles**:
   - `TextAppearance.GuardianAI.Title` - 24sp, bold, black
   - `TextAppearance.GuardianAI.Subtitle` - 14sp, gray (#999999)
   - `TextAppearance.GuardianAI.Body` - 16sp, black
   - `TextAppearance.GuardianAI.Label` - 14sp, black
   - `TextAppearance.GuardianAI.Link` - 14sp, blue, bold

6. **Status Badge** (`Widget.GuardianAI.StatusBadge`)
   - Rounded badge style
   - Used for online/offline/alert status

#### Spacing Constants:

- `guardianai_spacing_small`: 8dp
- `guardianai_spacing_medium`: 16dp
- `guardianai_spacing_large`: 24dp
- `guardianai_spacing_xlarge`: 32dp

#### Widget Layouts:

- `widget_primary_button.xml` - Primary button template
- `widget_secondary_button.xml` - Secondary button template
- `widget_card_container.xml` - Card container template
- `widget_status_badge.xml` - Status badge template

---

## Data Models

### Core Models

1. **User** (`data/model/User.java`)
   - Basic user information
   - Role (PARENT/CHILD)
   - Email, display name, UID

2. **ParentProfile** (`data/model/ParentProfile.java`)
   - Extended parent information
   - Profile picture, phone number
   - Total children count

3. **ChildProfile** (`data/model/ChildProfile.java`)
   - Extended child information
   - Device info, screen time, location
   - Online status

4. **ChildSummary** (`data/model/ChildSummary.java`)
   - High-level child summary for dashboard
   - Total screen time, alerts count
   - Device status

5. **Alert** (`data/model/Alert.java`)
   - Alert/notification model
   - Type, severity, read status

6. **DashboardSummary** (`data/model/DashboardSummary.java`)
   - Aggregated dashboard data
   - Total devices, alerts, screen time

7. **PolicyReference** (`data/model/PolicyReference.java`)
   - Reference to policies (no logic)
   - Links policies to child profiles

---

## Firebase Integration Layer

### Services

1. **AuthService** (`network/AuthService.java`)
   - User authentication
   - Login, signup, logout
   - Session management

2. **FCMTokenService** (`network/FCMTokenService.java`)
   - FCM token registration
   - Token storage in Firestore
   - Token refresh handling

3. **ChildProfileService** (`network/ChildProfileService.java`)
   - Child profile CRUD operations
   - Firestore integration

4. **DashboardService** (`network/DashboardService.java`)
   - Dashboard data aggregation
   - Real-time updates

5. **QRPairingService** (`network/QRPairingService.java`)
   - QR code generation
   - Device pairing
   - Child account creation

### Firestore Collections

```
users/                    # User accounts
  └── {uid}/
      - email
      - displayName
      - role
      - parentUid (for children)

parent_profiles/          # Parent extended profiles
  └── {parentUid}/
      - name
      - email
      - profilePictureUrl

child_profiles/          # Child extended profiles
  └── {profileId}/
      - childUid
      - parentUid
      - name
      - deviceName
      - screenTimeToday

fcm_tokens/               # FCM tokens for notifications
  └── {uid}/
      - token
      - platform
      - updatedAt

alerts/                   # Alerts/notifications
  └── {alertId}/
      - parentUid
      - childUid
      - type
      - severity

qr_pairing_tokens/        # QR pairing tokens
  └── {token}/
      - parentUid
      - expiresAt
```

---

## Theme Enforcement

### Color Palette

All colors are defined in `res/values/colors.xml`:

- **Primary**: `#4A90E2` (Blue)
- **Primary Dark**: `#3A7BC8`
- **Accent**: `#4A90E2`
- **Background**: `#F5F5F5` (Light gray)
- **Text Colors**: Black, Gray (#666666, #999999), White

### Typography

- **Title**: 24sp, bold, black
- **Subtitle**: 14sp, gray (#999999)
- **Body**: 16sp, black
- **Label**: 14sp, black
- **Link**: 14sp, blue, bold

### Spacing

- Use spacing constants from `styles.xml`
- Standard padding: 24dp (screens), 16dp (components)
- Standard margins: 8dp (small), 16dp (medium), 24dp (large)

### Components

- **Buttons**: 56dp height, 12dp corner radius
- **Cards**: 12dp corner radius, 2dp elevation
- **Input Fields**: White filled background, 12dp corner radius

---

## Usage Guidelines

### Creating New Activities

1. Extend `BaseActivity`
2. Override `setupUI()` method
3. Use theme styles from `styles.xml`
4. Use spacing constants

**Example**:
```java
public class MyActivity extends BaseActivity {
    @Override
    protected void setupUI() {
        // Setup UI
    }
}
```

### Creating New Fragments

1. Extend `BaseFragment`
2. Override `setupUI()` method
3. Check `isFragmentAttached()` before accessing context

**Example**:
```java
public class MyFragment extends BaseFragment {
    @Override
    protected void setupUI() {
        if (!isFragmentAttached()) return;
        // Setup UI
    }
}
```

### Creating New ViewModels

1. Extend `BaseViewModel`
2. Use LiveData for UI updates
3. Handle errors in `handleError()`

**Example**:
```java
public class MyViewModel extends BaseViewModel {
    private MutableLiveData<String> data = new MutableLiveData<>();
    
    public LiveData<String> getData() {
        return data;
    }
}
```

### Creating New Repositories

1. Extend `BaseRepository`
2. Use `getFirestore()` for Firestore access
3. Handle errors with `handleError()`

**Example**:
```java
public class MyRepository extends BaseRepository {
    public void fetchData(String id) {
        getFirestore().collection("my_collection")
            .document(id)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Handle success
                } else {
                    handleError(task.getException());
                }
            });
    }
}
```

### Using UI Components

1. **Primary Button**:
```xml
<com.google.android.material.button.MaterialButton
    style="@style/Widget.GuardianAI.PrimaryButton"
    android:text="Submit" />
```

2. **Card Container**:
```xml
<com.google.android.material.card.MaterialCardView
    style="@style/Widget.GuardianAI.CardContainer">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>
```

3. **Text Input**:
```xml
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.GuardianAI.TextInputLayout">
    <com.google.android.material.textfield.TextInputEditText />
</com.google.android.material.textfield.TextInputLayout>
```

---

## FCM Token Handling

### Initialization

Call `FCMTokenService.initializeToken()` after user login:

```java
FCMTokenService fcmService = new FCMTokenService();
fcmService.initializeToken();
```

### Cleanup

Call `FCMTokenService.deleteToken()` on logout:

```java
fcmService.deleteToken();
```

### Token Storage

Tokens are stored in Firestore collection `fcm_tokens` with document ID = user UID.

**Note**: Notification logic will be implemented in future modules. This service only handles token registration.

---

## Module Support

This foundation supports the following modules:

1. ✅ **Authentication & User Management** - Already implemented
2. ✅ **Parent Dashboard** - Foundation ready
3. ⏳ **Child Interaction & Enforcement Interface** - Foundation ready
4. ⏳ **App Usage & Screen Time Monitoring** - Foundation ready
5. ⏳ **App Access Control & Blocking** - Foundation ready
6. ⏳ **Location Tracking & Geo-Safety** - Foundation ready

---

## Best Practices

1. **Always extend base classes** - Use `BaseActivity`, `BaseFragment`, `BaseViewModel`, `BaseRepository`
2. **Use theme styles** - Never hardcode colors, sizes, or spacing
3. **Follow package structure** - Keep code organized by module
4. **Handle errors consistently** - Use base class error handling methods
5. **Check fragment attachment** - Always check `isFragmentAttached()` before accessing context
6. **Use LiveData** - For reactive UI updates in ViewModels
7. **Repository pattern** - Keep data access logic in repositories, not ViewModels

---

## Future Enhancements

- Navigation Component integration (optional)
- Dependency Injection (Dagger/Hilt) - if needed
- Room Database for local caching - if needed
- Coroutines/Flow support - if migrating to Kotlin

---

## Notes

- This foundation is designed for **Java** (not Kotlin)
- All UI components match the **Login Module theme**
- Foundation is **FYP-appropriate** - simple, clean, maintainable
- No business logic in foundation - only infrastructure

---

**Last Updated**: Foundation Creation Date
**Version**: 1.0.0








