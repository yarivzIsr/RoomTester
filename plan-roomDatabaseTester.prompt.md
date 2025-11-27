## Plan: Room Database Tester with Persistence Verification

Implement a single-screen Android app using Room database with transaction counter persistence testing capabilities. The app will test database survival across app restarts and device reboots using MVVM architecture, Kotlin Coroutines, and KSP for Room code generation.

### Steps

1. **Configure dependencies and KSP** - Add Room, ViewModel, KSP plugin to [libs.versions.toml](D:\tests\RoomTester\gradle\libs.versions.toml), [build.gradle.kts](D:\tests\RoomTester\build.gradle.kts), and [app/build.gradle.kts](D:\tests\RoomTester\app\build.gradle.kts); add REBOOT permission to [AndroidManifest.xml](D:\tests\RoomTester\app\src\main\AndroidManifest.xml)

2. **Create Room database layer** - Implement `TransactionEntity` with id, transactionEnumerator, dateTime fields; create `TransactionDao` with insert, deleteAll, getAll, getMaxEnumerator queries; build `AppDatabase` with KSP annotations in `app/src/main/java/com/test/roomtester/data/`

3. **Build Repository and ViewModel** - Create `TransactionRepository` wrapping DAO operations; implement `MainViewModel` with StateFlow for UI state, functions for addTransaction (increment counter logic), clearTable, toggleRecordsVisibility, reboot, shutdown, and pullPlug actions in `app/src/main/java/com/test/roomtester/`

4. **Implement UI screen** - Replace `MainActivity` content with `TransactionTesterScreen` composable containing Column with "Add Transaction", "Clear Table", "Show Records" buttons, LazyColumn for records display with formatted datetime, and power management buttons ("Restart Device", "Shut Down", "Pull the Plug") in [MainActivity.kt](D:\tests\RoomTester\app\src\main\java\com\test\roomtester\MainActivity.kt)

5. **Add dialogs and formatting** - Create `ConfirmationDialog` for clear table action, `PullPlugDialog` for power disconnect instructions; implement datetime formatter ("yyyy-MM-dd HH:mm:ss.SSS") and PowerManager integration with permission error handling in UI components

6. **Wire dependency injection** - Add Application class with database singleton initialization; inject repository into ViewModel; ensure proper lifecycle management and Flow collection in UI with proper CoroutineScope

### Further Considerations

1. **KSP Configuration** - Use `com.google.devtools.ksp` plugin version 2.0.0-1.0.21 (matching Kotlin 2.0.0) and `ksp()` configuration instead of `kapt()`

2. **Counter Logic Strategy** - Query max transactionEnumerator before insert; if null (empty table) start at 1, else increment by 1; wrap in database transaction for thread safety

3. **PowerManager Permission Handling** - REBOOT permission requires system app signature; gracefully catch SecurityException and show Toast explaining limitation when permission denied

4. **Testing Methodology** - Manual testing: verify counter continuity after app kill, device reboot, and power cycle; records persist in LazyColumn after operations; datetime displays milliseconds correctly

