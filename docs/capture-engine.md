# Hex Capture Engine

The `HexCaptureEngine` is the core business logic component of FitQuest. It is responsible for orchestrating hardware sensors and determining when a territory has been successfully captured.

## Core Responsibilities

1. **Sensor Orchestration**: Subscribes to `LocationTrackingManager` and `StepSensorManager` when tracking starts.
2. **Hex Identification**: Continuously maps the current GPS location to an H3 Hex ID.
3. **Step Accumulation**: Tracks how many steps are taken within specific hexagons during an active session.
4. **State Management**: Maintains the `HexCaptureSnapshot`, a thread-safe representation of the current tracking state.

## Tracking Lifecycle

### `startTracking()`
- Sets the `isTracking` flag to true.
- Launches two coroutines:
    - **Location Job**: Updates the `currentHexId` and `nearbyHexIds` whenever the GPS location changes.
    - **Steps Job**: Collects step deltas and increments the step count for the *current* hexagon.

### `stopTracking()`
- Cancels active sensor coroutines.
- Triggers the persistence of any hexes that met the capture threshold.
- Resets the tracking state.

## Capture Mechanics

A hexagon is considered "captured" during a session if the user takes a minimum number of steps while inside its boundary.

- **Capture Threshold**: Currently set in `CaptureScreenModel` (default: 1 step).
- **Session State**: The engine keeps a `Map<String, Int>` of `hexId -> steps` for the duration of the tracking session.

## Data Structures

### `HexCaptureSnapshot`
An immutable data class emitted by the engine's `StateFlow`.

```kotlin
data class HexCaptureSnapshot(
    val isTracking: Boolean = false,
    val currentHexId: String? = null,
    val currentLocation: GeoPoint? = null,
    val sessionSteps: Int = 0,
    val hexesToSteps: Map<String, Int> = emptyMap(),
    val nearbyHexIds: List<String> = emptyList()
)
```

## Thread Safety
The `HexCaptureEngine` uses `MutableStateFlow` with the `.update { ... }` atomic operator to ensure that concurrent updates from location and step sensors are handled correctly without race conditions.
