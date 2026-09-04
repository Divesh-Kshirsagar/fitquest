# ADR 0002: Standard Raster Map Architecture and Real Hardware Sensor Pipeline

## Context
1. MapLibre failed to render street details because the public vector demo tiles endpoint (`demotiles.maplibre.org`) only provided tiles up to zoom level 6. When the camera zoomed to street level (zoom 16.0), no tiles existed, rendering the canvas invisible.
2. The user required zero external `.env` or MapTiler API key dependencies for map rendering.
3. Steps were incrementing automatically due to a development step simulator (`DevStepSimulator` emitting 2 steps/second) and mock GPS updates (`DevLocationSimulator`), and hardcoded initial coordinates in `HexCaptureEngine` displayed fake hexagon IDs before real GPS fix.
4. The user requested complete elimination of all simulations and stubs so the application functions on real hardware sensors only.

## Decision
1. **Embedded Standard Map Style (`STANDARD_MAP_STYLE_JSON`)**:
   - Switched from remote style URLs to an embedded MapLibre style JSON specification using high-resolution OpenStreetMap CARTO Voyager raster tiles (`https://a.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png`).
   - Supports worldwide zoom levels 0 through 20 without requiring any API keys, accounts, or `.env` configuration.
   - Preserves custom H3 hexagon vector overlays (`nearby-hex-source`, `captured-hex-fill-layer`, `current-hex-fill-layer`) rendered directly on top of the raster tiles.
2. **Proper Compose AndroidView Lifecycle**:
   - Instantiated `MapView` via `remember` and attached it synchronously in `AndroidView.factory`.
   - Managed lifecycle transitions (`onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`) via `DisposableEffect` observing `LocalLifecycleOwner`.
3. **Pure Hardware Sensor Pipeline**:
   - `StepSensorManager`: Uses Android `Sensor.TYPE_STEP_COUNTER` with fallback to `Sensor.TYPE_STEP_DETECTOR`. If no hardware sensor is detected, the flow stays idle and never emits synthetic or timer-based steps.
   - `LocationTrackingManager`: Subscribes exclusively to `FusedLocationProviderClient` with `Priority.PRIORITY_HIGH_ACCURACY`. Added `getLastLocation` for immediate initial positioning.
   - `HexCaptureEngine`: Removed all references to `DevLocationSimulator` and hardcoded coordinates. Initiates passive real-time GPS monitoring on startup, displaying real hexagon IDs only after an authentic GPS fix is received.
   - `AppModule`: Removed `useDevLocation` and `useDevSteps` flags, wiring production sensor managers directly.

## Consequences & Validation
- Street, road, and city details render crisply worldwide at street zoom levels without an API key or `.env` file.
- The step counter and distance metrics stay at zero until the user physically walks with the device.
- Hexagon IDs and surrounding territory grid accurately reflect the user's real physical geographic location.
- `./gradlew :app:test` and `./gradlew :app:assembleDebug` compile and pass cleanly.
