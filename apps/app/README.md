# FitQuest Hex Capture Prototype

This app prototype implements a Room-backed Hex Capture loop:

- Step counter (`TYPE_STEP_COUNTER`) streams step deltas.
- Fused location streams current GPS position.
- `HexCaptureEngine` maps location to an H3 index and applies incoming steps to that hex.
- Session results are persisted in Room (`captured_hexes` table).
- Compose UI shows a MapLibre map with captured H3 hexes via GeoJSON.

## Main files

- `fitquest/src/main/java/com/example/mobileapp/core/capture/HexCaptureEngine.kt`
- `fitquest/src/main/java/com/example/mobileapp/core/data/local/`
- `fitquest/src/main/java/com/example/mobileapp/features/capture/CaptureScreenModel.kt`
- `fitquest/src/main/java/com/example/mobileapp/ui/capture/CurrentRunScreen.kt`

## TODO

- [ ] Android 15+ 16 KB page-size compliance: current bundled native lib `lib/arm64-v8a/libh3-java.so` is reported as not 16 KB aligned. Replace with a compliant upstream artifact or rebuild the native H3 library with 16 KB-compatible ELF load alignment before Play submission.

## Quick try

```bash
cd apps/app
./gradlew :app:test
```

For an Android install/run from Android Studio, open this project and run the `fitquest` module.
