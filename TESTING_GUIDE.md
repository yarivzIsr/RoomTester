# Room Database Persistence Testing Guide

## Overview
This guide explains how to test the Room database persistence with different methods, including workarounds for the REBOOT permission issue.

## Understanding the REBOOT Permission Issue

The `android.permission.REBOOT` permission is a **system-level permission** that can only be granted to:
1. System apps (pre-installed with the OS)
2. Apps signed with the system signature
3. Apps with root access

Regular apps installed via APK **cannot** use this permission, which is why you see the "permission denied" error.

## Testing Methods (From Easy to Advanced)

### ✅ Method 1: Force Close App (NO ROOT REQUIRED)
**This is the easiest and most practical method for testing persistence.**

1. **Add several transactions** using the "Add Transaction" button
2. Note the highest transaction number (e.g., Transaction #5)
3. Press **"Force Close App (Kill Process)"** button
   - This immediately kills the app process (simulates a crash)
4. **Reopen the app** from your launcher
5. **Verify**: All transactions should still be there with the same numbers
6. **Add another transaction**: It should continue from the next number (e.g., #6)

**What this tests:**
- Database persistence after app process death
- Counter continuity after restart
- Data integrity

---

### ✅ Method 2: Manual App Force Stop (NO ROOT REQUIRED)
**Uses Android's built-in force stop feature.**

1. **Add several transactions**
2. Go to Android **Settings** → **Apps** → **Room Tester**
3. Tap **"Force Stop"**
4. Confirm the force stop
5. Return to home screen and **reopen the app**
6. **Verify**: All data persists

**What this tests:**
- Same as Method 1, but using system force stop

---

### ✅ Method 3: Manual Device Reboot (NO ROOT REQUIRED)
**The most reliable test for true persistence.**

1. **Add several transactions** (e.g., 5-10 transactions)
2. **Note the last transaction number**
3. Press and hold the **Power button**
4. Select **"Restart"** or **"Reboot"**
5. Wait for device to fully restart
6. **Open the app** again
7. **Verify**: All transactions still present

**What this tests:**
- Database survival across device reboot
- File system persistence
- Complete cold start scenario

---

### 🔧 Method 4: Root Access Methods (REQUIRES ROOT)
**Only if your device is rooted.**

#### Option A: Using the app buttons
1. Install the APK on your rooted device
2. Grant root access when prompted
3. Press **"Restart Device (Root/System)"** button
   - The app will attempt to use `su -c reboot` command
4. Device should reboot
5. Reopen app and verify data

#### Option B: ADB with root
```bash
adb shell
su
reboot
```

**What this tests:**
- Full device reboot persistence
- Automated testing capability

---

### ⚡ Method 5: Pull the Plug (ADVANCED - HARDWARE)
**For testing unexpected power loss scenarios.**

1. **Use only on test devices or emulators**
2. Add several transactions
3. Press **"Pull the Plug Instructions"** button to see the warning
4. For physical devices:
   - Remove the battery (if removable) while app is running
   - Or unplug while low on battery
5. For emulators:
   - Close emulator window abruptly (X button)
   - Or use AVD Manager to stop emulator
6. Restart device/emulator
7. Open app and verify data

**What this tests:**
- Database corruption resistance
- WAL (Write-Ahead Logging) effectiveness
- SQLite crash recovery

---

## Expected Results for All Methods

### ✅ Success Criteria:
1. **All transactions persist** after restart
2. **Transaction counter continues** from where it left off
3. **Timestamps remain accurate**
4. **No data corruption** or loss
5. **Database ID sequence** continues properly

### ❌ If Data is Lost:
This should NOT happen with Room database. If it does:
1. Check if app data was cleared (Settings → Apps → Clear Data)
2. Verify the app isn't running in "Don't keep activities" developer mode
3. Check if storage is full
4. Review logcat for database errors

---

## Recommended Testing Sequence

For comprehensive persistence testing, follow this order:

1. **Start simple**: Use "Force Close App" button (5 tests)
   - Add 3 transactions → Force close → Verify → Repeat 5 times
   
2. **System force stop**: Use Settings → Force Stop (3 tests)
   - Add 5 transactions → Force stop → Verify → Repeat 3 times

3. **Device reboot**: Manual reboot (2 tests)
   - Add 10 transactions → Reboot → Verify → Repeat 2 times

4. **Clear and restart**: Full cycle
   - Clear all data using "Clear Table"
   - Add transactions
   - Restart (any method)
   - Verify counter starts from 1 again

---

## Workarounds for Non-Rooted Devices

Since you cannot use the reboot buttons without root/system access, here are alternatives:

### Option 1: Use the "Force Close App" Button
- **Fastest** and **most practical**
- Works on all devices
- Tests 90% of persistence scenarios

### Option 2: Enable Developer Options
1. Go to Settings → About Phone
2. Tap "Build Number" 7 times
3. Go to Settings → Developer Options
4. Enable **"Don't keep activities"**
5. Every time you leave the app, it's destroyed
6. Perfect for rapid testing

### Option 3: Battery Saver Mode
Some devices aggressively kill apps in battery saver mode:
1. Enable battery saver
2. Open another heavy app
3. System may kill Room Tester
4. Reopen to test persistence

---

## Understanding Database Persistence

Room database uses SQLite, which persists data to disk at:
```
/data/data/com.test.roomtester/databases/transaction_database
```

This file survives:
- ✅ App process death
- ✅ Force stop
- ✅ Device reboot
- ✅ Most crashes
- ❌ App uninstall
- ❌ Clear app data
- ❌ Factory reset

---

## Testing Log Template

Use this to track your tests:

```
Test #1: Force Close App
- Added: Transactions 1-5
- Method: Force Close button
- Result: ✅ All data persisted
- New transaction: #6 (correct sequence)

Test #2: Manual Reboot
- Added: Transactions 6-15
- Method: Power button → Restart
- Result: ✅ All data persisted
- New transaction: #16 (correct sequence)

Test #3: Pull the Plug
- Added: Transactions 16-20
- Method: Removed battery during write
- Result: ✅ All data persisted
- Notes: Room WAL mode protected the data
```

---

## FAQ

**Q: Why can't I use the reboot button?**
A: REBOOT permission requires system-level access. Use "Force Close App" instead.

**Q: Is "Force Close" as good as a reboot test?**
A: For database persistence, yes! It tests the same scenario: cold start with no running process.

**Q: Will the database survive if I clear app data?**
A: No, clearing app data deletes the database. This is by design.

**Q: How do I root my device?**
A: Rooting is device-specific and voids warranty. For testing, use emulators or the force close method.

**Q: Can I make this a system app?**
A: Yes, but it requires:
- Rooted device
- Moving APK to /system/app/
- Setting proper permissions
- Rebooting device
Not recommended unless you know what you're doing.

---

## Summary

**Best Method for Most Users:** Use the **"Force Close App (Kill Process)"** button

This provides:
- ✅ No root required
- ✅ Instant testing
- ✅ Reliable results
- ✅ Safe and reversible
- ✅ Tests real-world app lifecycle

The reboot buttons are provided for completeness but are not necessary for valid persistence testing.

