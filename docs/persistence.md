# Persistence

FitQuest uses a local Room database to persist captured hexagons and step counts, ensuring that progress is maintained across app restarts.

## Database Schema

### `CapturedHexEntity`
The primary table for storing territory data.

- **`hexId`** (`String`, Primary Key): The H3 hexadecimal representation of the cell.
- **`totalSteps`** (`Int`): Cumulative steps taken by the user within this specific hexagon across all sessions.
- **`lastUpdated`** (`Long`): Timestamp of the most recent steps addition.

## Data Access Object (`HexDao`)

The `HexDao` provides transactional methods for interacting with the database:

- **`observeAllCapturedHexes()`**: Returns a `Flow<List<CapturedHexEntity>>`, allowing the UI to reactively update whenever the database changes.
- **`addSteps(hexId, steps, timestamp)`**: A custom `@Transaction` method that retrieves the current step count for a hex, merges it with new session data, and performs an upsert.

## Repository Pattern

The `HexRepository` acts as an abstraction layer between the logic engine and the data source.

### `RoomHexRepository`
- **Observing**: Forwards the flow of captured hexes to the UI.
- **Merging**: When a tracking session ends, the `HexCaptureEngine` passes a map of `hexId -> steps` to the repository. The repository iterates through this map and calls `hexDao.addSteps()` for each entry.

## Initialization

The database is initialized via Koin in [AppModule.kt](file:///home/divesh/Desktop/projects/fitquest/apps/app/fitquest/src/main/java/com/example/mobileapp/di/AppModule.kt):

```kotlin
single {
    Room.databaseBuilder(get(), FitQuestDatabase::class.java, "fitquest.db")
        .fallbackToDestructiveMigration() // TODO(DB-PROD): Implement migrations for release
        .build()
}
single { get<FitQuestDatabase>().hexDao() }
```

> [!NOTE]
> Currently, `fallbackToDestructiveMigration()` is enabled for development convenience. This means that schema changes will result in data loss unless proper migrations are implemented later.
