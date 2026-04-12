# FitQuest

A gamified fitness app where you capture hexagonal territories by walking through them.

## How It Works

1. The map is divided into hexagons using [Uber H3](https://h3geo.org/).
2. Walk inside a hexagon — your steps are counted via the device's step-counter sensor.
3. The user with the most steps in a hex becomes the "king" of that territory.

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android device or emulator (API 24+)
- A [MapTiler](https://www.maptiler.com/) API key

### Setup

1. Copy `.env.example` to `.env` inside `apps/app/`:
   ```bash
   cp apps/app/.env.example apps/app/.env
   ```
2. Add your MapTiler API key to the `.env` file:
   ```
   MAPTILER_API_KEY=your_key_here
   ```
3. Open `apps/app/` in Android Studio and sync Gradle.

### H3 Native Libraries

The `h3-android` dependency requires native `.so` libraries. They are manually bundled in:
```
fitquest/src/main/jniLibs/
├── arm64-v8a/libh3-java.so
└── armeabi-v7a/libh3-java.so
```

> **Note:** If you need to support x86/x86_64 emulators, you'll need to extract and add the corresponding `.so` files from the H3 Android AAR.

## Emulator & Testing Notes

### Step Counter Sensor

Android emulators **do not** have a hardware step-counter sensor (`TYPE_STEP_COUNTER`).
To test the step-tracking and hex-capture flow on an emulator, use the built-in dev simulators:

1. Open `di/AppModule.kt`
2. Set the dev toggles:
   ```kotlin
   val useDevLocation = true   // mock GPS walk path
   val useDevSteps = true      // synthetic step counter (2 steps/sec)
   ```
3. Build and run. The app will simulate walking along a predefined path and emit synthetic steps.

> **TODO:** The default walk path is configured in `DevLocationSimulator.DEFAULT_WALK_PATH` (currently set to Connaught Place, Delhi). Change this to a location near you for easier testing.

### Location on Emulator

Even without the dev location simulator, you can inject GPS coordinates into the emulator:
- **Android Studio:** Extended Controls (⋯) → Location → set lat/lng and click "Send"
- **ADB:** `adb emu geo fix <longitude> <latitude>`

### H3 Resolution

The H3 resolution is currently set to **10** (hex edge ≈ 65 m) for faster capture feedback during development.
This may be changed after field testing — see the `TODO(TESTING)` comment in `HexCaptureEngine.kt`.

### Grid Ring Size

The nearby hex grid shows **k=2** rings around the user (~19 hexes).
This can be tuned in `HexCaptureEngine.kt` — see the `TODO(TESTING)` comment.

## Architecture

```
com.example.mobileapp/
├── core/
│   ├── capture/          # HexCaptureEngine — combines location + steps → hex ownership
│   ├── data/local/       # Room database, DAO, repository for persisted hex data
│   ├── dev/              # Dev simulators (mock GPS, mock steps) — DEBUG only
│   ├── geo/              # H3 hex indexer, GeoJSON mapper
│   ├── model/            # Domain models (GeoPoint)
│   ├── permissions/      # Runtime permission helper
│   └── sensors/          # Location & step sensor managers
├── di/                   # Koin dependency injection module
├── features/capture/     # CaptureScreenModel (Orbit MVI), CaptureState
└── ui/
    ├── capture/          # CurrentRunScreen (Compose + MapLibre)
    └── theme/            # Material 3 theme
```

## License

See [LICENSE](LICENSE).