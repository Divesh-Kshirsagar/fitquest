# Graph Report - fitquest  (2026-09-04)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 360 nodes · 681 edges · 23 communities (19 shown, 4 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 19 edges (avg confidence: 0.53)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `405858cd`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- quests/router.py
- map/router.py
- GeoPoint
- seed.py
- CurrentRunScreen.kt
- users/router.py
- AppModule.kt
- MainHubScreen.kt
- StepSensorManager.kt
- MainActivity.kt
- api/router.py
- HexGeoJsonMapper
- gradlew
- fitquest/build.gradle.kts
- ExampleInstrumentedTest
- ExampleUnitTest

## God Nodes (most connected - your core abstractions)
1. `User` - 14 edges
2. `GeoPoint` - 13 edges
3. `HexOwnership` - 12 edges
4. `UserQuest` - 10 edges
5. `CaptureScreenModel` - 10 edges
6. `enroll_user_quest_endpoint()` - 9 edges
7. `update_user_quest()` - 9 edges
8. `get_hexes_for_viewport()` - 9 edges
9. `process_run_sync()` - 9 edges
10. `Quest` - 8 edges

## Surprising Connections (you probably didn't know these)
- `process_run_sync()` --uses--> `HexOwnership`  [INFERRED]
  apps/api/app/modules/runs/service.py → apps/api/app/modules/map/models.py
- `create_hex()` --uses--> `HexOwnership`  [INFERRED]
  apps/api/app/modules/map/service.py → apps/api/app/modules/map/models.py
- `get_hex_by_id()` --uses--> `HexOwnership`  [INFERRED]
  apps/api/app/modules/map/service.py → apps/api/app/modules/map/models.py
- `get_hexes_for_user()` --uses--> `HexOwnership`  [INFERRED]
  apps/api/app/modules/map/service.py → apps/api/app/modules/map/models.py
- `update_hex()` --uses--> `HexOwnership`  [INFERRED]
  apps/api/app/modules/map/service.py → apps/api/app/modules/map/models.py

## Import Cycles
- None detected.

## Communities (23 total, 4 thin omitted)

### Community 0 - "quests/router.py"
Cohesion: 0.11
Nodes (42): SQLModel, Quest, The global definition of a challenge. Created by the admin/system., Tracks a specific user's progress on a specific quest., UserQuest, create_quest_endpoint(), enroll_user_quest_endpoint(), get_quest_endpoint() (+34 more)

### Community 1 - "map/router.py"
Cohesion: 0.11
Nodes (40): HexOwnership, SQLModel, The 'King of the Hill' table. Represents the CURRENT owner of a specific H3…, create_hex_endpoint(), get_hex_endpoint(), get_user_hexes_endpoint(), get, MapViewportResponse (+32 more)

### Community 2 - "GeoPoint"
Cohesion: 0.07
Nodes (20): HexCaptureEngine, HexCaptureSnapshot, DevLocationSimulator, Flow, Location, HexIndexer, HexIndexer, UberH3HexIndexer (+12 more)

### Community 3 - "seed.py"
Cohesion: 0.08
Nodes (33): Any, get_current_user(), get_db(), Session, Dev stub — returns a fixed user. Replace with real auth in production., Settings, create_db_and_tables(), get_session() (+25 more)

### Community 4 - "CurrentRunScreen.kt"
Cohesion: 0.09
Nodes (23): FitQuestApi, MapViewportResponse, RunSyncPayload, RunSyncSummary, HeatmapResponse, HexDetailResponse, MapViewportResponse, RunSyncPayload (+15 more)

### Community 5 - "users/router.py"
Cohesion: 0.16
Nodes (29): Core User model with Profile stats, Streak logic, and Relationships., User, create_user_endpoint(), get_user_endpoint(), get_users_endpoint(), get, patch, post (+21 more)

### Community 6 - "AppModule.kt"
Cohesion: 0.12
Nodes (10): Application, CapturedHexEntity, FitQuestDatabase, HexDao, Flow, HexRepository, Flow, RoomHexRepository (+2 more)

### Community 7 - "MainHubScreen.kt"
Cohesion: 0.11
Nodes (14): Screen, LoginScreen, FriendsTab, Tab, TabOptions, Tab, TabOptions, LeaderboardTab (+6 more)

### Community 8 - "StepSensorManager.kt"
Cohesion: 0.18
Nodes (9): DevStepSimulator, Flow, PermissionManager, Flow, StepSensorManager, SensorEventListener, Context, Sensor (+1 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.31
Nodes (6): MainActivity, Screen, WelcomeScreen, MobileAppTheme(), Bundle, ComponentActivity

### Community 11 - "HexGeoJsonMapper"
Cohesion: 0.53
Nodes (3): HexGeoJsonMapper, HexIndexer, FeatureCollection

### Community 12 - "gradlew"
Cohesion: 0.60
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **2 isolated node(s):** `HeatmapResponse`, `HexDetailResponse`
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CaptureScreenModel` connect `CurrentRunScreen.kt` to `AppModule.kt`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **Why does `GeoPoint` connect `GeoPoint` to `CurrentRunScreen.kt`?**
  _High betweenness centrality (0.039) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `HexOwnership` (e.g. with `create_hex()` and `get_hex_by_id()`) actually correct?**
  _`HexOwnership` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `HeatmapResponse`, `HexDetailResponse` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `quests/router.py` be split into smaller, more focused modules?**
  _Cohesion score 0.11207729468599034 - nodes in this community are weakly interconnected._
- **Should `map/router.py` be split into smaller, more focused modules?**
  _Cohesion score 0.10887949260042283 - nodes in this community are weakly interconnected._
- **Should `GeoPoint` be split into smaller, more focused modules?**
  _Cohesion score 0.06659619450317125 - nodes in this community are weakly interconnected._