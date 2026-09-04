# ADR 0001: MapLibre Rendering Lifecycle & Tile Fallback Architecture

## Context
When running FitQuest on an Android emulator or device without a preconfigured `.env` containing a valid `MAPTILER_API_KEY`, the territory capture map appeared blank/black and failed to render:
1. MapTiler returned HTTP 403 Forbidden because `BuildConfig.MAPTILER_STYLE_URL` defaulted to `https://api.maptiler.com/maps/streets-v2/style.json?key=`. MapLibre failed style loading silently without falling back.
2. `MapView` in Jetpack Compose was instantiated inside a `LaunchedEffect(Unit)`. By the time `LaunchedEffect` executed, the screen's `LifecycleOwner` was already in the `RESUMED` state. The lifecycle observer only responded to forward state changes, meaning `mapView.onStart()` and `mapView.onResume()` were never invoked.
3. Before the user clicked "Start Capture", `HexCaptureEngine.state.value.currentLocation` was `null`. This defaulted the camera target to `(0.0, 0.0)` in the Atlantic Ocean with an empty hex layer collection.
4. `useDevLocation` was set to `false`, causing emulators without hardware GPS or external mock location providers to remain stationary with null coordinates.

## Decision
1. **Open Tile Fallback in BuildConfig**:
   In `apps/app/fitquest/build.gradle.kts`, check if `MAPTILER_API_KEY` is present and non-empty. If absent, fallback to `https://demotiles.maplibre.org/style.json`, which is an open, unauthenticated tile service.
2. **Explicit MapView Lifecycle Initialization**:
   Inside the Jetpack Compose `CaptureMap` composable, explicitly invoke `mapView.onStart()` and `mapView.onResume()` immediately after view creation, while retaining the `DisposableEffect` lifecycle observer for subsequent background/foreground events and `mapView.onDestroy()`.
3. **Pre-populate Initial Camera Coordinates & Nearby Hexes**:
   Initialize `HexCaptureEngine` with the first waypoint of `DevLocationSimulator.DEFAULT_WALK_PATH` (37.7749, -122.4194) and index its surrounding radius (radius 3) upon initialization. When `CaptureMap` loads, set the camera center to `currentLocation` at zoom level 16.0.
4. **Enable Dev Location by Default for Emulator/Indoor Testing**:
   In `AppModule.kt`, configure `useDevLocation = true` so developers and testers can verify route capture and territory conquers without requiring outdoor GPS movement.

## Consequences & Validation
- Map canvas renders reliably immediately upon entering the Recon tab even without an API key.
- Territory conquest hexes render overlaid on the map canvas prior to starting the run.
- Walk simulation proceeds smoothly at 1-second intervals along the configured GPS path.
