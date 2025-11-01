# 🖍 Kids Drawing & Coloring App

A **kid-friendly Android drawing and coloring app** built in **Kotlin**.
Children can **import images and color them in** using brushes and an eraser — perfect for digital coloring pages on tablets.

---

## ✨ Features (MVP)

### In-Scope
- 🎨 **Import background image:** Load PNG/JPG files from device storage
- ✏️ **Draw on transparent layer:** Brush tool overlays strokes over the imported image
- 🧽 **Eraser tool:** Removes drawn strokes only, preserves background
- 🔍 **Pan & Zoom:** Pinch-to-zoom and drag to navigate large images
- ↩️ **Undo/Redo:** Step backward or forward through drawing history
- 💾 **Save/Export:** Export finished artwork as flattened PNG to device gallery
- 👶 **Kid-friendly UI:** Large touch targets (≥48 dp) and minimal interface optimized for tablets

### Out-of-Scope (Future)
- Fill/bucket tool, stickers, text
- Multi-page projects or layers
- Cloud sync and parental controls
- Advanced brush types (airbrush, watercolor, etc.)

---

## 🧱 Technical Specifications

| Item | Value |
|------|--------|
| **Language** | Kotlin |
| **IDE** | Android Studio Koala or newer |
| **JDK** | 17 + |
| **Build System** | Gradle (Android Gradle Plugin 8.2 +) |
| **Compile SDK** | 35 (Android 15) |
| **Target SDK** | 35 |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Architecture** | Single-Activity + Custom View + MVVM |
| **State Management** | AndroidX ViewModel + LiveData/StateFlow (no DI framework in MVP) |

### Dependencies
```groovy
implementation "androidx.core:core-ktx:1.13.1"
implementation "androidx.appcompat:appcompat:1.7.0"
implementation "com.google.android.material:material:1.12.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4"
implementation "androidx.activity:activity-ktx:1.9.0"
implementation "androidx.fragment:fragment-ktx:1.8.1"
implementation "androidx.constraintlayout:constraintlayout:2.1.4"
implementation "androidx.recyclerview:recyclerview:1.3.2"
```

---

## 🗂 Project Structure
```
com.brett.coloringkids
├── ui
│   ├── MainActivity.kt                  # Host activity
│   ├── DrawingFragment.kt               # Main drawing UI
│   ├── DrawingViewModel.kt              # State + logic
│   ├── views/DrawingView.kt             # Custom canvas (draw, pan, zoom)
│   └── adapters/ColorPaletteAdapter.kt  # RecyclerView adapter for colors
├── domain/model
│   ├── Stroke.kt                        # Path + paint snapshot
│   ├── Tool.kt                          # Pen/Eraser enum
│   └── CanvasState.kt                   # Canvas state holder
├── data
│   ├── ImageRepository.kt               # Image import (SAF)
│   └── ExportRepository.kt              # MediaStore save/export
└── util
    └── BitmapUtils.kt                   # Flatten + bitmap utilities
```

---

## ⚙️ Developer Setup

1. **Install Android Studio Koala or newer**
   [developer.android.com/studio](https://developer.android.com/studio)

2. **Install SDK & tools**
   - Android SDK Platform 35 (Android 15)
   - Android SDK Build-Tools 34 +
   - Android Emulator (optional for testing)

3. **JDK 17 + required** (bundled with Android Studio Koala or newer)

4. **Clone and open the project**
   ```bash
   git clone <repository-url>
   cd kids_draw
   ```

5. **Open** `kids_draw` in Android Studio → *Sync Gradle*
6. **Run or build APK** (`Build → Build Bundle(s)/APK(s) → Build APK(s)`)

---

## 🧩 Build & Install

### Command-Line (Debug)
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Release Build (Signed)
```bash
# create keystore once
keytool -genkey -v -keystore release.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
# edit keystore.properties accordingly
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

---

## 🖥 How to Use

1. **Launch the app** → "Coloring" icon
2. **Import image** → tap 🖼 → select picture from gallery/files
3. **Draw:**
   - Brush tool (default)
   - Choose color (8-color palette)
   - Adjust brush size (2 – 40 px)
4. **Navigate:**
   - Pinch to zoom, two-finger drag to pan
   - Double-tap to fit to screen
5. **Undo / Redo** → tap arrows
6. **Erase** → switch to eraser tool
7. **Save** → tap 💾 → stored under `Pictures/ColoringKids/`

---

## 🧠 Architecture Overview

### Drawing Engine
- **Two-layer system:**
  - *Background bitmap* (immutable imported image)
  - *Stroke bitmap* (transparent ARGB_8888 for drawing)
- Canvas composites both each frame.
- **Brush:** circular, anti-aliased stroke.
- **Eraser:** same brush using `PorterDuff.Mode.CLEAR`.

### Gesture System
- `Matrix` (`contentMatrix`) for transform
- `ScaleGestureDetector` → pinch zoom
- `GestureDetector` → pan & fling
- Pointer coordinates mapped to content-space via inverse matrix
- Brush width remains **visually constant** across zoom levels

### Undo / Redo
- Two stacks (`undoStack`, `redoStack`) of immutable `Stroke` objects
- Undo → pop undoStack → replay remaining strokes
- Redo → pop redoStack → reapply strokes

### Palette Implementation
- Horizontal RecyclerView of preset color swatches (8 colors)
- Tap → update ViewModel `currentColor`

### Lifecycle & State
- ViewModel retains in-memory state across rotation & background
- Active drawing is auto-saved in memory; persisted file save planned post-MVP
- No DI framework (Hilt / Koin) — manual ViewModelProvider instantiation

---

## 🗄 Storage & Permissions

| Operation | Implementation | Permission |
|------------|----------------|-------------|
| **Import** | `ACTION_OPEN_DOCUMENT` (SAF) | none |
| **Persist access** | `takePersistableUriPermission()` | none |
| **Export** | MediaStore insert (`Pictures/ColoringKids`) | none |
| **Legacy storage** | 🚫 Not used (scoped storage safe) |

---

## ✅ Testing Checklist

- [ ] Import large images (4–12 MP) without OOM
- [ ] Brush width constant while zooming
- [ ] Undo/redo correct for 50 + strokes
- [ ] Eraser preserves background
- [ ] Saved PNG dimensions correct
- [ ] App survives rotation / background
- [ ] No unexpected permission dialogs
- [ ] Offline-only functionality OK

### Tested on
- **Pixel Tablet (2023)** Android 14
- **Samsung Tab A7 Lite** Android 12

---

## 🚧 Known Limitations (MVP)

1. Images > 4096 px downscaled to avoid OOM
2. Undo replay cost grows > 100 strokes
3. No fill tool or advanced brush types
4. Single page only

---

## 🚀 Future Enhancements

- Fill tool (flood fill algorithm)
- Sticker library and text tool
- Multi-page projects (save/load state)
- Share / print / cloud backup
- Parental controls (screen time / content filter)
- Advanced brush effects (airbrush, patterns)

---

## 📸 Screenshots
*(Add these once available)*
```
/docs/screenshots/
 ├── import.png
 ├── draw.png
 └── save.png
```

Example Markdown embed:

```markdown
![Drawing Screen](docs/screenshots/draw.png)
```

---

## 🤝 Contributing
1. Fork the repo → create feature branch
2. Follow **Kotlin Coding Conventions + Android KTX** style
3. PR titles use Conventional Commits (`feat:`, `fix:`, `docs:`)
4. Include screenshot/GIF for UI changes

---

## 🪪 License

```
MIT License

Copyright (c) 2025 Brett

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📬 Contact
**Developer:** Brett Adam
**Email:** <your-contact@example.com>
**GitHub:** [github.com/brettadam](https://github.com/brettadam)
