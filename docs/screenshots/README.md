# Screenshots

This directory contains screenshots of the Kids Drawing & Coloring App.

## Expected Files

- `import.png` - Screenshot showing the import image functionality
- `draw.png` - Screenshot showing the drawing interface with color palette
- `save.png` - Screenshot showing the save/export functionality

## Screenshot Guidelines

### Format
- **Format:** PNG
- **Orientation:** Landscape (tablet optimized)
- **Resolution:** 1920x1200 or higher recommended
- **Device:** Preferably Pixel Tablet or similar

### Content
1. **import.png:** Show the file picker dialog or import button highlighted
2. **draw.png:** Show the main drawing interface with:
   - Imported coloring page image
   - Color palette (RecyclerView with 8 colors)
   - Brush size slider
   - Tool buttons (Pen/Eraser)
   - Some drawn strokes to demonstrate functionality

3. **save.png:** Show either:
   - Save button highlighted
   - Success message after saving
   - Gallery view with saved image

### Capturing Screenshots

#### From Android Studio Emulator
1. Run the app on an emulator
2. Navigate to the desired screen
3. Click the camera icon in the emulator toolbar
4. Save to this directory

#### From Physical Device
1. Use `adb` command:
   ```bash
   adb shell screencap -p /sdcard/screenshot.png
   adb pull /sdcard/screenshot.png docs/screenshots/<filename>.png
   ```

2. Or use device's native screenshot function:
   - Most devices: Power + Volume Down
   - Transfer via USB or cloud storage

### Privacy
- Ensure no personal information is visible in screenshots
- Use sample coloring page images (royalty-free)
- Do not include identifiable device information

## Usage in README

Once screenshots are added, they'll automatically be referenced in the main README.md:

```markdown
![Drawing Screen](docs/screenshots/draw.png)
```
