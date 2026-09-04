# ADR 0001: Project Grounding and Territory Capture Engine Architecture

## Context & Problem Statement
FitQuest is a gamified fitness application combining hardware step sensors and GPS tracking to drive real-world hexagonal territory conquest. As the repository was recently cloned, a canonical architectural decision record is needed to establish the foundational technical patterns across both the Android mobile client and the FastAPI backend service.

## Decisions

### 1. Spatial Partitioning: Uber H3
- **Choice**: Uber H3 discrete global grid system.
- **Resolution**: Resolution 10 (~65.8m edge length, ~0.015 km² area) for active development and rapid capture feedback; Resolution 9 (~174m edge length) designated for production scale.
- **Grid Disk**: Visible neighborhood set to $k=2$ rings (19 hexagons total) around current user position.
- **Rendering**: Handled via `HexGeoJsonMapper` emitting MapLibre GeoJSON feature collections on background coroutine dispatchers to eliminate UI thread frame drops.

### 2. Mobile Client Architecture
- **Presentation**: Jetpack Compose with Voyager navigation (`ScreenModel`, `TabNavigator`) and Orbit MVI (`ContainerHost<CaptureState, Nothing>`).
- **Core Engine**: `HexCaptureEngine` as single reactive state coordinator ingesting cold flows from `LocationTrackingManager` and `StepSensorManager`.
- **Local Persistence**: Android Room (`FitQuestDatabase`, `CapturedHexEntity`, `HexDao`) maintaining cumulative steps and captured territories across device restarts.
- **Dependency Injection**: Koin (`appModule`) providing singletons for database, repo, indexer, engine, and factory-scoped Voyager ScreenModels.

### 3. Backend API Architecture
- **Framework**: FastAPI with SQLModel (SQLAlchemy + Pydantic) on Python 3.12.
- **Database**: PostgreSQL hosted on Supabase with PostGIS and H3-pg extensions, connected via `asyncpg`.
- **Authentication**: Supabase JWT verification (`get_current_user` dependency validating bearer tokens).
- **Turf War Ingestion**: `POST /api/v1/runs/sync` processes session step payloads, executing conditional upserts on `HexOwnership` (capturing unclaimed hexes, reinforcing owned hexes, or stealing rival hexes when defense steps are exceeded).

### 4. Verification & Testing Guardrails
- **Mobile Unit Tests**: Gradle `:app:test` task verified against Android SDK 35/36.
- **Emulator Sensor Simulation**: `DevLocationSimulator` (mock GPS loop) and `DevStepSimulator` (synthetic 2 steps/sec) toggled via `AppModule.kt`.

## Consequences & Follow-ups
- Need to resolve 16 KB page-size compliance for bundled native H3 `.so` libraries before Android 15 production distribution.
- Implement an offline outbox queue in Room for run syncs when network connectivity is lost.
- Expand backend viewport queries to utilize PostGIS bounding box filters and H3 hierarchical rollups for low-zoom city views.
