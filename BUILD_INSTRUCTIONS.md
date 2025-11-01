# Quick Start Build Guide

## For Developers (Android Studio)

### Step 1: Install Prerequisites
1. Download and install [Android Studio](https://developer.android.com/studio)
2. During installation, ensure you install:
   - Android SDK
   - Android SDK Platform (API 35)
   - Android Virtual Device

### Step 2: Open Project
```bash
# Clone or download this repository
cd kids_draw

# Open in Android Studio
# File → Open → Select this folder
```

### Step 3: Sync & Build
1. Android Studio will prompt to sync Gradle files - click "Sync Now"
2. Wait for sync to complete (downloads dependencies)
3. Click Build → Make Project (or Ctrl+F9 / Cmd+F9)

### Step 4: Run
**On Physical Device:**
1. Enable Developer Options on your Android device:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Click Run ▶️ button in Android Studio
5. Select your device from the list

**On Emulator:**
1. Tools → Device Manager → Create Device
2. Select tablet (e.g., "Pixel Tablet") for best experience
3. Select system image (API 26-35)
4. Click Run ▶️ and select emulator

### Step 5: Generate APK for Distribution
```bash
# Debug APK (for testing)
Build → Build Bundle(s) / APK(s) → Build APK(s)
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (for distribution - requires signing)
# See README.md for keystore setup
Build → Generate Signed Bundle / APK
```

## For Command-Line Builds

### Prerequisites
- Java JDK 11 or newer
- Android SDK (set `ANDROID_HOME` environment variable)

### Build Debug APK
```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install to Connected Device
```bash
./gradlew installDebug

# Or manually with adb
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Clean Build
```bash
./gradlew clean assembleDebug
```

## Troubleshooting

### "SDK not found"
Set `ANDROID_HOME` environment variable:
```bash
# Linux/Mac (~/.bashrc or ~/.zshrc)
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Windows (System Environment Variables)
ANDROID_HOME=C:\Users\<YourName>\AppData\Local\Android\Sdk
```

### "Gradle sync failed"
1. Check internet connection (Gradle needs to download dependencies)
2. File → Invalidate Caches / Restart
3. Delete `.gradle` folder and sync again

### "Unsupported class file version"
Ensure you're using JDK 11 or newer:
```bash
java -version  # Should show 11 or higher
```

Update JDK in Android Studio:
- File → Project Structure → SDK Location → JDK Location

### Build is slow
Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
```

## Sideloading to Tablet (No Android Studio)

### Method 1: ADB (Technical)
1. Install Android SDK Platform Tools
2. Enable USB Debugging on tablet
3. Connect tablet via USB
4. Run: `adb install app-debug.apk`

### Method 2: File Transfer (Non-Technical)
1. Copy `app-debug.apk` to tablet (USB, email, cloud drive)
2. On tablet:
   - Settings → Security → Unknown Sources → Enable
   - OR Settings → Apps → Special Access → Install Unknown Apps → Enable for file manager
3. Open file manager, tap APK file
4. Tap "Install"

## Build Output Sizes (Approximate)
- Debug APK: ~5-10 MB
- Release APK (minified): ~3-7 MB

## Next Steps
See [README.md](README.md) for:
- Feature overview
- Architecture details
- Usage guide
- Testing checklist
