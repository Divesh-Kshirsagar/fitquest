# ADR 0003: MapTiler Vector Streets Integration & Complete Backend Decoupling

## Context
1. Log analysis revealed that MapLibre failed style loading with HTTP status code 403 because `apps/app/.env.example` defined `MAPTILER_STYLE_URL=https://api.maptiler.com/maps/streets/style.json?key=${MAPTILER_API_KEY}`. When `.env` was created, Gradle did not expand the shell variable expression `${MAPTILER_API_KEY}`, causing the literal string `"${MAPTILER_API_KEY}"` to be requested rather than the user's authentic API key `[REDACTED_KEY]`.
2. Log analysis revealed repeated OkHttp network errors:
   `--> GET http://127.0.0.1:8000/api/v1/map/viewport?...`
   `<-- HTTP FAILED: java.net.UnknownServiceException: CLEARTEXT communication to 127.0.0.1 not permitted by network security policy`.
   These occurred because `CaptureMap` invoked `screenModel.onMapIdle` on every camera movement and `api.syncRunSession` upon finishing runs, attempting to communicate with an unbuilt local backend.
3. The user requested vector map rendering (MapTiler Streets v2 with vector tiles, text labels, building outlines, and smooth zooming) and complete removal of all backend dependencies from the mobile frontend.

## Decision
1. **Robust Vector Style Resolution & Variable Expansion in Gradle**:
   - Updated `apps/app/fitquest/build.gradle.kts` to search candidate `.env` paths (`rootProject`, `project`, `apps/app/`, etc.).
   - Explicitly expanded `${MAPTILER_API_KEY}` and `$MAPTILER_API_KEY` occurrences.
   - Automatically resolved `MAPTILER_STYLE_URL` to `https://api.maptiler.com/maps/streets-v2/style.json?key=$mapTilerApiKey` when the API key is present.
   - If no API key is provided, gracefully defaulted to OpenFreeMap Liberty vector style (`https://tiles.openfreemap.org/styles/liberty`), ensuring 100% vector map rendering in all environments without raster artifacts.
2. **Complete Removal of Backend Dependencies**:
   - Removed `FitQuestApi`, `OkHttpClient`, and `Retrofit` single definitions from `AppModule.kt`.
   - Removed `FitQuestApi` injection, `onMapIdle`, and `api.syncRunSession` from `CaptureScreenModel.kt`.
   - Removed `onCameraIdleListener` from `CaptureMap` in `CurrentRunScreen.kt`.
   - All session data, conquered hexagons, player profiles, streaks, and achievements are persisted entirely locally via Android Room repositories.
3. **Vector Style Loading with Runtime Fallback**:
   - In `CurrentRunScreen.kt`, configured MapLibre to load the MapTiler Streets v2 vector style.
   - Added `addOnDidFailLoadingMapListener` on `MapView` to automatically switch to the open vector style if the primary endpoint experiences network disruption or rate limits.

## Consequences & Validation
- Log inspection confirmed `BuildConfig.MAPTILER_STYLE_URL` now contains the properly substituted key and targets `https://api.maptiler.com/maps/streets-v2/style.json?key=[REDACTED_KEY]`.
- Curl verification confirmed HTTP 200 OK for MapTiler Streets v2 vector style with vector fonts, sprites, and layers.
- Zero network requests are dispatched to `http://127.0.0.1:8000`.
- All 57 unit tests in `:app:test` passed, and `:app:assembleDebug` completed successfully.
