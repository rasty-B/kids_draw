# Changelog

All notable changes to the Kids Drawing & Coloring App will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- RecyclerView-based color palette with ColorPaletteAdapter
- ColorItem data class for palette state management
- Visual selection indicator for selected colors (white stroke)
- DiffUtil implementation for efficient palette updates
- MIT License (LICENSE file)
- Screenshots directory structure (docs/screenshots/)
- Screenshot capture guidelines (docs/screenshots/README.md)
- Contributing guidelines in README
- Contact information in README
- Emojis and tables in README for better readability

### Changed
- **BREAKING:** Upgraded JDK from 8 to 17
  - Updated `compileOptions` to `JavaVersion.VERSION_17`
  - Updated `kotlinOptions.jvmTarget` to `'17'`
  - Requires Android Studio Koala or newer
- Replaced individual color View elements with single RecyclerView
- Improved color palette UI with MaterialCardView (56dp swatches, 8dp radius)
- Enhanced touch feedback with ripple effects
- Updated README.md with modern formatting and comprehensive documentation
- Simplified fragment_drawing.xml (reduced from ~180 lines to ~100 lines for palette section)

### Fixed
- Improved color palette maintainability
- Better memory efficiency with RecyclerView recycling
- Cleaner separation of concerns in DrawingFragment

### Technical Details

#### Dependencies Added
- `androidx.recyclerview:recyclerview:1.3.2`

#### Files Created (5)
1. `LICENSE` - MIT License
2. `app/src/main/java/com/brett/coloringkids/ui/adapters/ColorPaletteAdapter.kt` - Color palette adapter
3. `app/src/main/res/layout/item_color.xml` - Color swatch layout
4. `docs/screenshots/.gitkeep` - Screenshots directory placeholder
5. `docs/screenshots/README.md` - Screenshot guidelines

#### Files Modified (4)
1. `app/build.gradle` - JDK 17 + RecyclerView dependency
2. `app/src/main/java/com/brett/coloringkids/ui/DrawingFragment.kt` - RecyclerView integration
3. `app/src/main/res/layout/fragment_drawing.xml` - RecyclerView palette layout
4. `README.md` - Comprehensive documentation update

#### Architecture Improvements
- Implemented ListAdapter pattern with DiffUtil
- Added proper ViewHolder pattern for color swatches
- Improved state management for selected color
- Better separation of UI and business logic
- Follows Material Design 3 guidelines

#### Performance Improvements
- RecyclerView more efficient than 8 individual Views
- DiffUtil minimizes unnecessary UI updates
- ListAdapter handles animations automatically
- Better memory usage with view recycling

#### Code Statistics
- Lines Added: +448
- Lines Removed: -272
- Net Change: +176 lines
- Files Changed: 9

## [1.0.0] - 2025-11-01

### Added
- Initial MVP release
- Import images (PNG/JPG) from device storage
- Draw on transparent layer with pen tool
- Eraser tool to remove strokes
- Pan and zoom with pinch gestures
- Undo/redo functionality with unlimited history
- Save/export as PNG to Pictures/ColoringKids/
- Kid-friendly UI with large touch targets (≥48dp)
- 8-color palette (black, red, blue, green, yellow, orange, purple, brown)
- Adjustable brush size (2-40px)
- Material Design 3 theming
- MVVM architecture
- Scoped storage compliance (no legacy permissions)
- Memory-safe image loading (max 4096px dimension)

### Technical Stack
- Kotlin 1.9.20
- Android Gradle Plugin 8.2.0
- Gradle 8.2
- Min SDK 26 (Android 8.0)
- Target SDK 35 (Android 15)
- AndroidX libraries
- Material Components

### Architecture
- Single-Activity + Fragment
- Custom DrawingView for canvas operations
- ViewModel for state management
- Repository pattern for data operations
- Two-layer rendering (background + strokes)
- Matrix-based pan/zoom transformations
- Stack-based undo/redo system

[Unreleased]: https://github.com/brettadam/kids_draw/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/brettadam/kids_draw/releases/tag/v1.0.0
