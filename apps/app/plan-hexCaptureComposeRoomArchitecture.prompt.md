`## Plan: Hex Capture Compose + Room

Implement the hex-capture loop end-to-end by first fixing project wiring, then adding Room persistence, sensor/location fusion, Orbit state, and MapLibre rendering with GeoJSON-fed H3 polygons. This keeps each layer independently testable (`data`, `engine`, `state`, `ui`) while matching your checkpoint flow and minimizing rework from build/config issues currently visible in the project scaffold.

### Steps
1. Normalize project module/build wiring in [`settings.gradle.kts`](settings.gradle.kts), [`build.gradle.kts`](build.gradle.kts), and [`gradle/libs.versions.toml`](gradle/libs.versions.toml), then add `MapLibre`, `Room`, and `KSP` dependencies in [`fitquest/build.gradle.kts`](fitquest/build.gradle.kts).
2. Create Room foundation in [`fitquest/src/main/java/com/example/mobileapp/core/data/local`](fitquest/src/main/java/com/example/mobileapp/core/data/local) with `CapturedHexEntity`, `HexDao` (`upsert/addSteps`), `FitQuestDatabase`, and repository abstractions.
3. Add capture inputs in [`fitquest/src/main/java/com/example/mobileapp/core/sensors`](fitquest/src/main/java/com/example/mobileapp/core/sensors) for `StepSensorManager` and `LocationTrackingManager`, and declare required permissions in [`fitquest/src/main/AndroidManifest.xml`](fitquest/src/main/AndroidManifest.xml).
4. Implement fusion logic in [`fitquest/src/main/java/com/example/mobileapp/core/capture/HexCaptureEngine.kt`](fitquest/src/main/java/com/example/mobileapp/core/capture/HexCaptureEngine.kt), combining step + location streams into `StateFlow` (`currentHex`, `hexesToSteps`) and session flush to DAO.
5. Build Orbit/Voyager state bridge in [`fitquest/src/main/java/com/example/mobileapp/features/capture`](fitquest/src/main/java/com/example/mobileapp/features/capture) with `CaptureState`, intents, and `ScreenModel` threshold handling for `capturedHexes`.
6. Replace starter UI in [`fitquest/src/main/java/com/example/mobileapp/MainActivity.kt`](fitquest/src/main/java/com/example/mobileapp/MainActivity.kt) with `CurrentRunScreen`, embed MapLibre `AndroidView`, and add GeoJSON conversion helper (H3 list -> `FeatureCollection`) plus `GeoJsonSource` + `FillLayer` updates.

### Further Considerations
1. Confirm module naming first: should the active Android module be `:fitquest` (current folder) or `:app` (current `settings` include)?
2. Pick capture tuning defaults now: H3 resolution + capture threshold (`50` steps?) to avoid downstream state/model churn.
3. Decide tracking scope: Option A in-foreground only, Option B foreground service for background capture, Option C hybrid with explicit user toggle.
4. Review this as a draft plan and tell me what to adjust before we move to execution details.
5. Add comments and notes or todos where further consideration is needed. Add comments where you are adding dependancies. Also do no write update anything important without my consent. 

