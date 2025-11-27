system app signature# Quick Start - Room Database Tester

## ⚡ QUICK TEST (2 minutes)

1. **Install** the APK: `app/build/outputs/apk/debug/app-debug.apk`

2. **Add transactions**: Tap "Add Transaction" 5 times
   - You should see: Transaction #1, #2, #3, #4, #5

3. **Kill the app**: Tap "Force Close App (Kill Process)"
   - App will immediately close

4. **Reopen app**: Tap the app icon from launcher
   - All 5 transactions should still be there!

5. **Continue**: Tap "Add Transaction" again
   - Should create Transaction #6 (not #1!)

✅ **Success!** Database persisted across app death and counter continued correctly.

---

## 🚫 REBOOT PERMISSION ERROR - SOLUTION

**Error you're seeing:**
```
"REBOOT permission denied. Requires system app signature."
```

**Why this happens:**
- REBOOT permission only works for system apps or rooted devices
- Your app is a regular user app

**✅ SOLUTION - Use "Force Close App" button instead:**
- Works on ALL devices (no root needed)
- Tests the SAME thing (database persistence)
- Faster than rebooting
- Just as reliable

**Still want to use reboot buttons?**
1. Root your device, OR
2. Test on an emulator with root, OR  
3. Just use manual reboot (Power button → Restart)

---

## 📱 Button Guide

| Button | Works Without Root? | Purpose |
|--------|-------------------|---------|
| Add Transaction | ✅ YES | Adds new record to database |
| Clear Table | ✅ YES | Deletes all records |
| Show/Hide Records | ✅ YES | Toggles record list visibility |
| **Force Close App** | ✅ **YES** | **Kills process - USE THIS!** |
| Restart Device | ❌ NO (needs root) | Reboots device |
| Shut Down Device | ❌ NO (needs root) | Powers off device |
| Pull the Plug | ✅ YES | Shows instructions |

---

## 📊 What You're Testing

Every time you use "Force Close App" and reopen:

1. ✅ Database file survives process death
2. ✅ All transaction data persists
3. ✅ Counter continues from correct number
4. ✅ Timestamps are preserved
5. ✅ No data corruption

This is the SAME as testing with reboot, but faster and doesn't require permissions!

---

## 🔧 Alternative Testing Methods

### Method 1: Android Force Stop
1. Add some transactions
2. Settings → Apps → Room Tester → Force Stop
3. Reopen app
4. Verify data persists

### Method 2: Manual Reboot
1. Add some transactions  
2. Hold Power button → Restart
3. Wait for device to reboot
4. Open app
5. Verify data persists

### Method 3: Developer Options
1. Settings → About → Tap Build Number 7 times
2. Settings → Developer Options → Don't keep activities
3. Every time you leave the app, it's destroyed
4. Return to app → Data should persist

---

## 💡 Pro Tips

- **Add 10-20 transactions** before testing to see clear results
- **Note the last transaction number** before closing app
- **Use "Clear Table"** to start fresh between tests
- **Check timestamps** to verify millisecond precision
- **Test multiple times** to ensure consistency

---

## ❓ FAQ

**Q: Does the app need to be a system app?**
A: No! Use "Force Close App" button instead.

**Q: Can I test without root?**
A: Yes! That's what the "Force Close App" button is for.

**Q: Is force close as good as reboot for testing?**
A: Yes! Both test database persistence from a cold start.

**Q: Why do I need REBOOT permission?**
A: You don't! It's optional. Use other methods.

---

## 📄 More Info

- Full testing procedures: See `TESTING_GUIDE.md`
- Implementation details: See `IMPLEMENTATION_SUMMARY.md`
- Original requirements: See `plan-roomDatabaseTester.prompt.md`

---

**Ready?** Install the APK and press "Force Close App" to test persistence! 🚀

