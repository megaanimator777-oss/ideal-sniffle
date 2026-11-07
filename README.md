# MusicViz

Android Studio / GitHub Actions project for a VAZ-2110 trunk subwoofer visualizer.

## Build
Open this folder in Android Studio, or run:
```bash
gradle assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## Features
- Black/white VAZ-2110 rear-view visualizer with animated oversized subwoofer.
- Bass/treble controls using Android Equalizer when the device permits output-mix effects.
- Attempts system-output visualization using Android Visualizer session 0.
- Home-screen launcher widget.

## Android limitations
Android does not guarantee that third-party apps can capture or process audio from every other player. Output-mix Visualizer/Equalizer support varies by device, firmware, Android version, and player policy. On many devices, global equalization is blocked or has no effect on other apps. The widget is a launcher widget; Android home-screen widgets cannot reliably render a smooth high-frame-rate live animation. The app remains usable as a standalone visualizer/demo when system audio capture is unavailable.
