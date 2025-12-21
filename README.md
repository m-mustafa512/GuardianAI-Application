# Guardian AI - Final Year Project

## Project Overview

Guardian AI is a parental control and privacy protection mobile application designed to work on **two separate Android devices**:
1. **Parent App** - Dashboard for monitoring and control
2. **Child App** - Monitoring, AI detection, and enforcement

## Architecture

### Project Structure
```
Guardian AI - APP/
├── child-app/          # Child device application
│   ├── data/          # Data layer (models, repositories, local DB)
│   ├── ui/            # UI layer (activities, fragments, viewmodels)
│   ├── services/      # Background monitoring services
│   ├── ai/            # TensorFlow Lite models and inference
│   ├── utils/         # Utility classes
│   └── network/       # Firebase integration
│
└── parent-app/        # Parent device application
    ├── data/          # Data layer (models, repositories, local DB)
    ├── ui/            # UI layer (activities, fragments, viewmodels)
    ├── utils/         # Utility classes
    └── network/       # Firebase integration
```

### Technology Stack

#### Core Technologies
- **Language**: Kotlin (preferred) / Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Material Design 3

#### Firebase Services
- **Firebase Authentication** - User authentication
- **Firebase Firestore** - Device pairing, alerts, rule synchronization
- **Firebase Cloud Messaging (FCM)** - Push notifications for alerts

#### Local Storage
- **Room Database** - Local data persistence on both devices
- **SQLite** - Used by Room under the hood

#### AI/ML
- **TensorFlow Lite** - On-device AI inference
- **LSTM Autoencoder** - Behavior anomaly detection (sensor & app usage data)
- **Audio Keyword Detection** - Pretrained lightweight models

## Key Features

### Child App
- Continuous monitoring of device usage
- On-device AI anomaly detection
- Rule enforcement (app blocking, time limits, etc.)
- Local data storage (privacy-focused)
- Background service for 24/7 monitoring

### Parent App
- Dashboard for viewing child device status
- Real-time alerts for risky behavior
- Rule configuration and management
- Device pairing and management
- Historical data visualization

## Setup Instructions

### Prerequisites
1. Android Studio (latest version)
2. JDK 11 or higher
3. Firebase project (create at https://console.firebase.google.com)

### Firebase Setup
1. Create a Firebase project
2. Add both Android apps (parent-app and child-app) to the project
3. Download `google-services.json` for each app
4. Place `google-services.json` in:
   - `child-app/`
   - `parent-app/`

### Building the Project
1. Clone or open this project in Android Studio
2. Sync Gradle files
3. Add `google-services.json` files (see Firebase Setup)
4. Build and run

## Permissions

### Child App Permissions
- `INTERNET` - Firebase connectivity
- `ACCESS_FINE_LOCATION` - Location monitoring
- `RECORD_AUDIO` - Audio keyword detection
- `READ_PHONE_STATE` - Call monitoring
- `READ_SMS` - SMS monitoring
- `READ_CALL_LOG` - Call log access
- `PACKAGE_USAGE_STATS` - App usage monitoring
- `FOREGROUND_SERVICE` - Background monitoring
- `POST_NOTIFICATIONS` - Alert notifications

### Parent App Permissions
- `INTERNET` - Firebase connectivity
- `ACCESS_NETWORK_STATE` - Network status

## Development Notes

### For FYP Scope
- This is an academic project, not a commercial product
- Some features may be simulated or partially implemented
- Focus on demonstrating core concepts and architecture
- Code is well-commented for academic review

### Architecture Principles
- **Separation of Concerns**: Clear separation between UI, business logic, and data
- **Dependency Injection**: Consider using manual DI or Hilt for FYP
- **Reactive Programming**: Use LiveData/Flow for data observation
- **On-Device Processing**: AI models run locally, no cloud inference

## Module Information

### Application IDs
- **Child App**: `com.mustafa.guardianai.child`
- **Parent App**: `com.mustafa.guardianai.parent`

### Minimum SDK
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 36
- **compileSdk**: 36

## Next Steps

1. ✅ Project structure setup
2. ✅ Dependencies configuration
3. ⏳ Firebase integration
4. ⏳ Authentication implementation
5. ⏳ Device pairing system
6. ⏳ Room database setup
7. ⏳ AI model integration
8. ⏳ Monitoring services
9. ⏳ UI implementation
10. ⏳ Testing and documentation

## License

This is a university Final Year Project. All rights reserved.

## Author

Mustafa - Final Year Project 2024


