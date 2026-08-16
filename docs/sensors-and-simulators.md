# Sensors and Simulators

FitQuest relies on hardware sensors to track user movement. To facilitate development and testing on emulators (which often lack specialized hardware), the app includes a comprehensive "simulated" sensor system.

## Hardware Sensors

### Location (`LocationTrackingManager`)
Uses Google Play Services Location API (or the standard Android `LocationManager`) to obtain GPS coordinates.
- **Accuracy**: Balanced power/accuracy for background tracking.
- **Frequency**: Configured to emit updates every few seconds or upon significant displacement.

### Step Counter (`StepSensorManager`)
Uses the Android hardware step-counter sensor (`Sensor.TYPE_STEP_COUNTER`).
- **Delta Calculation**: Since the hardware sensor provides a cumulative count since boot, the `StepSensorManager` calculates the "delta" (steps taken since the last update) to provide real-time feedback.
- **Availability**: This sensor is required for normal gameplay. If missing, the app will throw an `IllegalStateException` unless the simulator is enabled.

---

## Development Simulators

Simulators allow developers to test the full "Capture" lifecycle without physically walking or using a real device.

### Location Simulator (`DevLocationSimulator`)
Emits a sequence of `Location` objects along a predefined GPS path.
- **Default Path**: A small loop in Connaught Place, Delhi.
- **Customization**: You can generate your own paths at [geojson.io](https://geojson.io) and update the `DEFAULT_WALK_PATH` in the class.
- **Frequency**: Emits 1 location point every 2 seconds.

### Step Simulator (`DevStepSimulator`)
Emits synthetic step deltas at a constant rate.
- **Default Rate**: 2 steps per second (approximating a slow walk).
- **Use Case**: Essential for testing on emulators where `TYPE_STEP_COUNTER` is unavailable.

---

## Configuration

Simulators are toggled in the Koin [AppModule.kt](file:///home/divesh/Desktop/projects/fitquest/apps/app/fitquest/src/main/java/com/example/mobileapp/di/AppModule.kt).

To enable simulators for testing:

```kotlin
val useDevLocation = true  // Mock GPS walk path
val useDevSteps = true     // Synthetic steps (2/sec)

// ...

single { StepSensorManager(get(), useDevSimulator = useDevSteps) }
single { LocationTrackingManager(get(), useDevSimulator = useDevLocation) }
```

> [!WARNING]
> Ensure these flags are set to `false` before building a production APK or distributing the app to real testers.
