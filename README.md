# Room Database Tester for Android

An Android application designed to test Room database persistence across device power cycles, app restarts, and unexpected terminations.

## 🎯 Purpose

This app tests whether Room database survives:
- ✅ App force close / process kill
- ✅ Device reboot
- ✅ Unexpected power loss ("pull the plug" scenario)
- ✅ Transaction counter continuity after restarts

## 📱 Features

### Transaction Management
- **Add Transaction** - Creates new transaction with auto-incrementing counter and millisecond-precision timestamp
- **Clear Table** - Deletes all records (with confirmation dialog)
- **Show/Hide Records** - Toggle visibility of transaction history

### Persistence Testing
- **Force Close App** - Instantly kills app process to test database survival (works on all devices)
- **Restart Device** - Attempts device reboot via root or PowerManager
- **Shut Down Device** - Attempts device shutdown via root or PowerManager
- **Pull the Plug** - Shows instructions for physical power disconnect test

### Records Display
- Scrollable list of all transactions
- Shows transaction number and formatted datetime (yyyy-MM-dd HH:mm:ss.SSS)
- Real-time updates via Flow-based reactive UI
- Empty state when no records exist

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0.0
- **UI**: Jetpack Compose with Material3
- **Database**: Room 2.6.1 with KSP code generation
- **Architecture**: MVVM with Repository pattern
- **Async**: Kotlin Coroutines and Flow
- **Min SDK**: 33 (Android 13)
- **Target SDK**: 35 (Android 14)

## 📋 Database Schema

### TestTransaction Entity
```kotlin
@Entity(tableName = "test")
data class TestTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionEnumerator: Long,    // Auto-incrementing counter
    val dateTime: Long                  // Epoch timestamp with milliseconds
)
```

## 🚀 Quick Start

### Installation
1. Download `app-debug.apk` from releases
2. Install on Android device (API 33+)
3. Open the app

### Testing Database Persistence

**Method 1: Force Close App (Recommended - Works on All Devices)**
1. Tap "Add Transaction" 5 times
2. Note the transaction numbers (1-5)
3. Tap "Force Close App (Kill Process)"
4. Reopen the app
5. ✅ Verify: All 5 transactions still visible
6. Tap "Add Transaction" → Should create #6

**Method 2: Manual Reboot**
1. Add 10 transactions
2. Press Power button → Restart
3. Wait for device to reboot
4. Open app
5. ✅ Verify: All transactions persist

See [TESTING_GUIDE.md](TESTING_GUIDE.md) for comprehensive testing procedures.

## 📖 Documentation

- **[QUICK_START.md](QUICK_START.md)** - 2-minute quick test guide
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Comprehensive testing methods and procedures
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Technical implementation details
- **[REBOOT_PERMISSION_SOLUTION.md](REBOOT_PERMISSION_SOLUTION.md)** - Explains REBOOT permission and workarounds
- **[APP_STATUS_REPORT.md](APP_STATUS_REPORT.md)** - Current app status and functionality

## 🔒 Permissions

- `android.permission.REBOOT` - Required for restart/shutdown buttons (system apps only)
  - **Note**: Regular apps cannot use this permission
  - Use "Force Close App" button instead (works without root)

## 🏗️ Architecture

```
app/src/main/java/com/test/roomtester/
├── MainActivity.kt              # UI and Compose screens
├── MainViewModel.kt             # State management and business logic
├── RoomTesterApplication.kt     # Application class with DI
└── data/
    ├── TestTransaction.kt       # Entity class
    ├── TransactionDao.kt        # Database operations
    ├── AppDatabase.kt           # Room database
    └── TransactionRepository.kt # Repository pattern
```

### MVVM Pattern
- **Model**: Room database entities and DAOs
- **ViewModel**: MainViewModel with StateFlow for reactive UI
- **View**: Jetpack Compose UI in MainActivity

### Key Features
- Singleton database pattern
- Flow-based reactive queries
- Coroutines for async operations
- Proper lifecycle management
- Error handling with user feedback

## 🧪 Testing Scenarios

1. **App Process Death**: Force close → Relaunch → Verify data
2. **Device Reboot**: Manual restart → Verify data
3. **Counter Continuity**: Counter increments correctly after restart
4. **Empty State**: Clear table → Force close → Verify empty
5. **Timestamp Accuracy**: Millisecond precision maintained

## ⚙️ Build Instructions

### Prerequisites
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK 35

### Build Commands
```bash
# Clone the repository
git clone https://github.com/yarivzIsr/RoomTester.git
cd RoomTester

# Build debug APK
gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Gradle Configuration
- Kotlin: 2.0.0
- Android Gradle Plugin: 8.8.0
- KSP: 2.0.0-1.0.21
- Room: 2.6.1

## 💡 How It Works

### Counter Logic
1. Query database for `MAX(transactionEnumerator)`
2. If null (empty table), start at 1
3. Otherwise, increment by 1
4. Insert new record with counter and current timestamp

### Persistence Mechanism
Room uses SQLite which persists to:
```
/data/data/com.test.roomtester/databases/transaction_database
```

This file survives:
- ✅ App process death
- ✅ Device reboot
- ✅ Most crashes
- ❌ App uninstall or clear data

## 🐛 Known Limitations

1. **REBOOT Permission**: Restart/Shutdown buttons require root or system signature
   - **Workaround**: Use "Force Close App" button (same test, no root needed)

2. **System Apps Only**: PowerManager reboot requires system-level access
   - **Alternative**: Manual reboot or use root access

3. **Root Access**: Some features work better on rooted devices
   - **Not Required**: Core functionality works without root

## 🤝 Contributing

Contributions welcome! Areas for improvement:
- Additional persistence test scenarios
- UI enhancements
- Export/import database functionality
- Statistics and analytics

## 📄 License

This is a testing application created for educational and development purposes.

## 👤 Author

Yariv Ziporin (yariv@isrcorp.co.il)

## 🔗 Links

- **Repository**: https://github.com/yarivzIsr/RoomTester
- **Issues**: https://github.com/yarivzIsr/RoomTester/issues

## 🎉 Success Criteria

The app is working correctly if:
- ✅ Transactions persist after force close
- ✅ Counter continues from correct number
- ✅ Timestamps are accurate to milliseconds
- ✅ Clear table removes all data
- ✅ Database survives device reboot

---

**Built with ❤️ using Kotlin and Jetpack Compose**

