# Kids Drawing & Coloring App

A simple Android drawing/coloring app built in Kotlin that allows kids to import images and color over them.

## Features (MVP)

### In-Scope
- **Import Background Image**: Load PNG/JPG images from device storage
- **Draw on Transparent Layer**: Use pen tool to draw over imported images
- **Eraser Tool**: Remove strokes without affecting the background
- **Pan & Zoom**: Navigate large images with pinch-to-zoom and pan gestures
- **Undo/Redo**: Step backward/forward through drawing history
- **Save/Export**: Save finished artwork as flattened PNG to device gallery
- **Kid-Friendly UI**: Large touch targets (≥48dp) optimized for tablets

### Out-of-Scope (Future)
- Fill/bucket tool, stickers, text
- Multi-page projects
- Cloud sync
- Advanced brush types (airbrush, patterns)

## Technical Specifications

### Platform & Tools
- **Language**: Kotlin
- **Build System**: Gradle (Android Gradle Plugin 8.2.0)
- **IDE**: Android Studio (latest stable)
- **Compile SDK**: 35 (Android 15)
- **Target SDK**: 35
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Architecture**: Single-Activity + Custom View + MVVM

### Dependencies
- AndroidX Core KTX 1.13.1
- AndroidX AppCompat 1.7.0
- Material Components 1.12.0
- Lifecycle ViewModel KTX 2.8.4
- Activity KTX 1.9.0
- Fragment KTX 1.8.1
- ConstraintLayout 2.1.4

### Project Structure
```
com.brett.coloringkids
├── ui
│   ├── MainActivity.kt                  # Entry point activity
│   ├── DrawingFragment.kt              # Main UI with controls
│   ├── DrawingViewModel.kt             # UI state & business logic
│   └── views/DrawingView.kt            # Custom canvas (drawing, pan, zoom)
├── domain/model
│   ├── Stroke.kt                       # Path + paint snapshot
│   ├── Tool.kt                         # Pen/Eraser enum
│   └── CanvasState.kt                  # Canvas state holder
├── data
│   ├── ImageRepository.kt              # Image loading (SAF)
│   └── ExportRepository.kt             # Save to MediaStore
└── util
    └── BitmapUtils.kt                  # Flatten layers
```

## Build Instructions

### Prerequisites
1. **Install Android Studio**: Download from [developer.android.com](https://developer.android.com/studio)
2. **Install Android SDK**:
   - Open Android Studio
   - Go to Tools → SDK Manager
   - Install SDK Platform 35 (Android 15)
   - Install SDK Platform 26 (Android 8.0) for minimum support
   - Install Android SDK Build-Tools 34.x or newer

### Build & Run

#### Option 1: Build via Android Studio
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd kids_draw
   ```

2. Open project in Android Studio:
   - File → Open → Select `kids_draw` folder

3. Sync Gradle:
   - Click "Sync Now" banner (or File → Sync Project with Gradle Files)

4. Build APK:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

5. Run on device/emulator:
   - Connect Android device via USB (with USB debugging enabled)
   - OR create an Android Virtual Device (AVD) in Tools → Device Manager
   - Click "Run" (▶️) button

#### Option 2: Build via Command Line
```bash
# From project root
./gradlew assembleDebug

# APK output: app/build/outputs/apk/debug/app-debug.apk
```

#### Option 3: Build Release APK (Signed)
1. Generate keystore (first time only):
   ```bash
   keytool -genkey -v -keystore release.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Create `keystore.properties` in project root:
   ```properties
   storePassword=<your_store_password>
   keyPassword=<your_key_password>
   keyAlias=release
   storeFile=release.keystore
   ```

3. Build release:
   ```bash
   ./gradlew assembleRelease
   # Output: app/build/outputs/apk/release/app-release.apk
   ```

### Sideload to Tablet

#### Via USB (ADB)
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Via File Transfer
1. Copy `app-debug.apk` to tablet (USB, cloud, email)
2. On tablet: Settings → Security → Install unknown apps → Enable for file manager
3. Open APK with file manager → Install

## How to Use

1. **Launch App**: Tap "Coloring" icon
2. **Import Image**: Tap "Import" → Select image from gallery/files
3. **Draw**:
   - Use pen tool (default) to draw
   - Select color from palette (8 colors available)
   - Adjust brush size with slider (2-40 px)
4. **Navigate**:
   - Pinch to zoom in/out
   - Two-finger drag to pan
   - Double-tap to fit image to screen
5. **Undo/Redo**: Tap arrows to step through history
6. **Erase**: Switch to eraser tool (removes strokes only, preserves background)
7. **Save**: Tap "Save" → Image saved to `Pictures/ColoringKids/`

## Architecture Details

### Drawing System
- **Two-Layer Approach**:
  - Background layer: Immutable bitmap (imported image)
  - Stroke layer: ARGB_8888 bitmap where drawing occurs
- **On-Screen Rendering**: Composite both layers via Canvas
- **Brush**: Circular stroke with configurable width
- **Eraser**: Same brush using `PorterDuff.Mode.CLEAR` xfermode

### Pan/Zoom Implementation
- Uses `Matrix` for content transformation (contentMatrix)
- `ScaleGestureDetector` for pinch-to-zoom
- `GestureDetector` for pan/fling gestures
- Touch coordinates mapped from screen-space → content-space via inverse matrix
- Brush width remains constant in content-space (visually consistent while zooming)

### Undo/Redo
- **Data Structure**: Two stacks (`undoStack`, `redoStack`) of `Stroke` objects
- **Stroke**: Immutable snapshot of path points + brush params
- **Undo**: Pop from `undoStack` → push to `redoStack` → replay all remaining strokes
- **Redo**: Pop from `redoStack` → push to `undoStack` → replay
- **Replay**: Redraw all strokes onto fresh transparent bitmap

### Storage (Scoped Storage Safe)
- **Import**: `ACTION_OPEN_DOCUMENT` (no permissions needed)
- **Persistent Permission**: `takePersistableUriPermission()` to retain access
- **Export**: MediaStore API (no `WRITE_EXTERNAL_STORAGE` permission required)
- **Output Path**: `Pictures/ColoringKids/coloring_yyyyMMdd_HHmmss.png`

## Testing Checklist

- [ ] Import large images (4-12 MP) without OOM crash
- [ ] Brush width remains visually constant when zooming
- [ ] Undo/redo works correctly with 50+ strokes
- [ ] Eraser removes strokes only (background preserved)
- [ ] Saved PNG has correct dimensions and no gaps
- [ ] App survives rotation/backgrounding (lifecycle safe)
- [ ] No unexpected permission dialogs
- [ ] Works offline (no network required)

## Known Limitations (MVP)

1. **Memory Constraints**: Images > 4096px longest edge are downscaled to prevent OOM
2. **Undo Performance**: Replay-based undo can be slow with 100+ strokes (optimize later)
3. **Single Page**: No multi-page project support
4. **Basic Tools**: Only pen + eraser (no fill, airbrush, etc.)

## Future Enhancements

- [ ] Fill/bucket tool with flood fill algorithm
- [ ] Sticker library (drag-and-drop)
- [ ] Text tool with font selection
- [ ] Multi-page projects (save/load state)
- [ ] Share to social media
- [ ] Parental controls (screen time, content filter)
- [ ] Cloud backup/sync
- [ ] Advanced brushes (airbrush, watercolor, patterns)

## License

MIT License (or specify your license)

## Contact

Developer: Brett
App: Kids Drawing & Coloring App
Package: com.brett.coloringkids
