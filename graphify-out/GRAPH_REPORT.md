# Graph Report - fitquest  (2026-09-04)

## Corpus Check
- 103 files · ~27,409 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 673 nodes · 1128 edges · 44 communities (40 shown, 4 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 19 edges (avg confidence: 0.53)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7ab98158`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- quests/router.py
- map/router.py
- GeoPoint
- seed.py
- AppModule.kt
- users/router.py
- HexRepository
- OnboardingScreen.kt
- StepSensorManager.kt
- UserProfileEntity
- AchievementEntity
- HomeTab.kt
- gradlew
- fitquest/build.gradle.kts
- ExampleInstrumentedTest
- ExampleUnitTest
- RunSessionEntity
- CRITICAL PROTOCOL: WORKSPACE STATE GUARDRAILS & CACHE BOUNDARY RETENTION
- Backend Architecture & Schema (FastAPI + Supabase)
- FitQuest Agent Context
- FitQuest API Backend Documentation
- Hexagonal Grid System
- README.md
- Hex Capture Engine
- Decisions
- Feature Sprint Requirements: Full Local Frontend Experience
- Sensors and Simulators
- Implementation Plan: Offline Frontend Sprint
- FitQuest
- Persistence
- ProgressionLogicTest
- FitQuest Hex Capture Prototype
- Decisions
- HexGeoJsonMapper
- FitQuest Agent Execution Ledger
- ADR 0001: MapLibre Rendering Lifecycle & Tile Fallback Architecture
- 📡 API Contracts (Mobile App DTOs)

## God Nodes (most connected - your core abstractions)
1. `UserProfileEntity` - 24 edges
2. `RunSessionEntity` - 15 edges
3. `UserProfileRepository` - 15 edges
4. `User` - 14 edges
5. `CaptureScreenModel` - 14 edges
6. `GeoPoint` - 13 edges
7. `HexOwnership` - 12 edges
8. `AchievementEntity` - 12 edges
9. `RunSessionRepository` - 11 edges
10. `RoomUserProfileRepository` - 11 edges

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

## Communities (44 total, 4 thin omitted)

### Community 0 - "quests/router.py"
Cohesion: 0.11
Nodes (42): SQLModel, Quest, The global definition of a challenge. Created by the admin/system., Tracks a specific user's progress on a specific quest., UserQuest, create_quest_endpoint(), enroll_user_quest_endpoint(), get_quest_endpoint() (+34 more)

### Community 1 - "map/router.py"
Cohesion: 0.11
Nodes (40): HexOwnership, SQLModel, The 'King of the Hill' table. Represents the CURRENT owner of a specific H3…, create_hex_endpoint(), get_hex_endpoint(), get_user_hexes_endpoint(), get, MapViewportResponse (+32 more)

### Community 2 - "GeoPoint"
Cohesion: 0.07
Nodes (20): HexCaptureEngine, HexCaptureSnapshot, Job, DevLocationSimulator, Flow, Location, HexIndexer, HexIndexer (+12 more)

### Community 3 - "seed.py"
Cohesion: 0.06
Nodes (32): Any, get_current_user(), get_db(), Session, Dev stub — returns a fixed user. Replace with real auth in production., Settings, create_db_and_tables(), get_session() (+24 more)

### Community 4 - "AppModule.kt"
Cohesion: 0.08
Nodes (24): Application, FitQuestApi, MapViewportResponse, RunSyncPayload, RunSyncSummary, HeatmapResponse, HexDetailResponse, MapViewportResponse (+16 more)

### Community 5 - "users/router.py"
Cohesion: 0.13
Nodes (32): Friendship, SQLModel, Tracks friend requests and accepted friends. Composite primary key prevents…, Core User model with Profile stats, Streak logic, and Relationships., User, create_user_endpoint(), get_user_endpoint(), get_users_endpoint() (+24 more)

### Community 6 - "HexRepository"
Cohesion: 0.19
Nodes (6): CapturedHexEntity, HexDao, Flow, HexRepository, Flow, RoomHexRepository

### Community 7 - "OnboardingScreen.kt"
Cohesion: 0.09
Nodes (21): PermissionManager, MainActivity, Screen, LoginScreen, Screen, OnboardingScreen, PermissionCard(), StepConcept() (+13 more)

### Community 8 - "StepSensorManager.kt"
Cohesion: 0.24
Nodes (7): DevStepSimulator, Flow, Flow, StepSensorManager, SensorEventListener, Sensor, SensorEvent

### Community 9 - "UserProfileEntity"
Cohesion: 0.07
Nodes (24): Flow, UserDao, UserProfileEntity, Flow, RoomUserProfileRepository, UserProfileRepository, Contender, ContenderRow() (+16 more)

### Community 10 - "AchievementEntity"
Cohesion: 0.08
Nodes (15): AchievementDao, Flow, AchievementEntity, AchievementRepository, Flow, RoomAchievementRepository, FitQuestDatabase, AchievementCard() (+7 more)

### Community 11 - "HomeTab.kt"
Cohesion: 0.11
Nodes (16): DailyQuestDao, Flow, DailyQuestEntity, Flow, QuestRepository, RoomQuestRepository, DailyProgressCard(), DailyQuestsCard() (+8 more)

### Community 12 - "gradlew"
Cohesion: 0.60
Nodes (3): gradlew script, die(), warn()

### Community 23 - "RunSessionEntity"
Cohesion: 0.13
Nodes (6): Flow, RunSessionDao, RunSessionEntity, Flow, RoomRunSessionRepository, RunSessionRepository

### Community 24 - "CRITICAL PROTOCOL: WORKSPACE STATE GUARDRAILS & CACHE BOUNDARY RETENTION"
Cohesion: 0.12
Nodes (16): 1. EPHEMERAL PROMPT CACHING RULES, 2. PRE-EXECUTION GROUNDING (TURN START), 3. BRANCH WORKING FILES, 4. ARCHITECTURE DECISION DOCUMENTATION, 5. POST-EXECUTION RECORD KEEPING (TURN COMPLETION), 6. Git Commit Policy, 7. graphify, Branch Completion Cleanup (+8 more)

### Community 25 - "Backend Architecture & Schema (FastAPI + Supabase)"
Cohesion: 0.18
Nodes (10): 1. Users Module Schema, 2. Map Module Schema, 3. Runs (Capture Sync) Module Schema, 4. Quests Module Schema, 🔐 Authentication Ecosystem, Backend Architecture & Schema (FastAPI + Supabase), 🗄 Database Schema (SQLModel + Postgres), 🏗 Modular Domain-Driven Architecture (+2 more)

### Community 26 - "FitQuest Agent Context"
Cohesion: 0.18
Nodes (10): ACTIVE WORKING MEMORY BLOCK (DYNAMIC SUFFIX), Active Working Objective, Backend SQLModel Entities, Codebase Metadata, Directory Topology, FitQuest Agent Context, Immutable Schemas & Contracts, Mobile Persistence Schema (Room) (+2 more)

### Community 27 - "FitQuest API Backend Documentation"
Cohesion: 0.18
Nodes (10): 1. Users Module (`app/modules/users`), 2. Map Module (`app/modules/map`), 3. Runs (Capture Sync) Module (`app/modules/runs`), 4. Quests Module (`app/modules/quests`), 📂 Architecture: Domain-Driven Design, 🔐 Auth & Database Connection, FitQuest API Backend Documentation, 🧩 Modules Breakdown (+2 more)

### Community 28 - "Hexagonal Grid System"
Cohesion: 0.18
Nodes (10): GeoJSON Mapping, Grid Ring Size (k-ring), Hexagonal Grid System, `HexGeoJsonMapper`, Native Libraries, Performance Considerations, Resolution, Spatial Configuration (+2 more)

### Community 29 - "README.md"
Cohesion: 0.20
Nodes (7): Architectural Patterns, Architecture Overview, Data Flow Diagram, Dependency Injection, MVI (Model-View-Intent), Reactive Streams (Coroutines & Flow), Voyager & ScreenModel

### Community 30 - "Hex Capture Engine"
Cohesion: 0.22
Nodes (9): Capture Mechanics, Core Responsibilities, Data Structures, Hex Capture Engine, `HexCaptureSnapshot`, `startTracking()`, `stopTracking()`, Thread Safety (+1 more)

### Community 31 - "Decisions"
Cohesion: 0.22
Nodes (8): 1. Spatial Partitioning: Uber H3, 2. Mobile Client Architecture, 3. Backend API Architecture, 4. Verification & Testing Guardrails, ADR 0001: Project Grounding and Territory Capture Engine Architecture, Consequences & Follow-ups, Context & Problem Statement, Decisions

### Community 32 - "Feature Sprint Requirements: Full Local Frontend Experience"
Cohesion: 0.22
Nodes (8): 1. Onboarding & Identity Flow, 2. Home Dashboard (`HomeTab.kt`), 3. Enhanced Run & Post-Run Experience (`CurrentRunScreen.kt`), 4. Leaderboard & Territory Rankings (`LeaderboardTab.kt`), 5. Profile & Achievements Tab (`ProfileTab.kt`), 6. Local Offline Persistence Layer, Branch Scope, Feature Sprint Requirements: Full Local Frontend Experience

### Community 33 - "Sensors and Simulators"
Cohesion: 0.22
Nodes (8): Configuration, Development Simulators, Hardware Sensors, Location (`LocationTrackingManager`), Location Simulator (`DevLocationSimulator`), Sensors and Simulators, Step Counter (`StepSensorManager`), Step Simulator (`DevStepSimulator`)

### Community 34 - "Implementation Plan: Offline Frontend Sprint"
Cohesion: 0.22
Nodes (8): 1. Local Database & Domain Persistence Layer, 2. Onboarding & Profile Setup Flow, 3. Home Dashboard (`HomeTab.kt`), 4. Enhanced Run & Post-Run Experience (`CurrentRunScreen.kt`), 5. Leaderboard & District Rankings (`LeaderboardTab.kt`), 6. Profile, Achievements & Settings (`ProfileTab.kt` & `AchievementsTab.kt`), 7. Verification & Testing, Implementation Plan: Offline Frontend Sprint

### Community 35 - "FitQuest"
Cohesion: 0.11
Nodes (18): Architecture, Commercial & Community Precedents, Documentation, Emulator & Testing Notes, FitQuest, Getting Started, Grid Ring Size, H3 Native Libraries (+10 more)

### Community 36 - "Persistence"
Cohesion: 0.25
Nodes (7): `CapturedHexEntity`, Data Access Object (`HexDao`), Database Schema, Initialization, Persistence, Repository Pattern, `RoomHexRepository`

### Community 38 - "FitQuest Hex Capture Prototype"
Cohesion: 0.40
Nodes (4): FitQuest Hex Capture Prototype, Main files, Quick try, TODO

### Community 39 - "Decisions"
Cohesion: 0.22
Nodes (8): 1. Local-First Room Persistence Expansion, 2. Onboarding & Dynamic App Initialization, 3. Active Run Experience & Post-Run Summary Modal, 4. Navigation Architecture & Tabs, ADR 0001: Offline-First Standalone Mobile Frontend Architecture, Consequences & Follow-ups, Context & Problem Statement, Decisions

### Community 40 - "HexGeoJsonMapper"
Cohesion: 0.53
Nodes (3): HexGeoJsonMapper, HexIndexer, FeatureCollection

### Community 41 - "FitQuest Agent Execution Ledger"
Cohesion: 0.40
Nodes (4): [2026-09-04 12:10] - Task: Initial Project Grounding, Context Scaffolding & Similar App References, [2026-09-04 14:00] - Task: Implement Complete Offline Mobile Frontend Experience, [2026-09-04 14:35] - Task: Diagnose and Fix Map Visibility in Recon/Run Screen, FitQuest Agent Execution Ledger

### Community 42 - "ADR 0001: MapLibre Rendering Lifecycle & Tile Fallback Architecture"
Cohesion: 0.40
Nodes (4): ADR 0001: MapLibre Rendering Lifecycle & Tile Fallback Architecture, Consequences & Validation, Context, Decision

### Community 43 - "📡 API Contracts (Mobile App DTOs)"
Cohesion: 0.50
Nodes (4): 📡 API Contracts (Mobile App DTOs), Map Viewports (Zoom-Aware), Runs Payload (The Sync Engine), Users (Flattened)

## Knowledge Gaps
- **113 isolated node(s):** `HexDetailResponse`, `HeatmapResponse`, `Codebase Metadata`, `Backend SQLModel Entities`, `Mobile Persistence Schema (Room)` (+108 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `UserProfileRepository` connect `UserProfileEntity` to `HomeTab.kt`, `AppModule.kt`, `OnboardingScreen.kt`?**
  _High betweenness centrality (0.025) - this node is a cross-community bridge._
- **Why does `UserProfileEntity` connect `UserProfileEntity` to `HomeTab.kt`, `OnboardingScreen.kt`?**
  _High betweenness centrality (0.024) - this node is a cross-community bridge._
- **Why does `HexRepository` connect `HexRepository` to `UserProfileEntity`, `GeoPoint`, `HomeTab.kt`, `AppModule.kt`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **What connects `HexDetailResponse`, `HeatmapResponse`, `Codebase Metadata` to the rest of the system?**
  _113 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `quests/router.py` be split into smaller, more focused modules?**
  _Cohesion score 0.11207729468599034 - nodes in this community are weakly interconnected._
- **Should `map/router.py` be split into smaller, more focused modules?**
  _Cohesion score 0.10887949260042283 - nodes in this community are weakly interconnected._
- **Should `GeoPoint` be split into smaller, more focused modules?**
  _Cohesion score 0.06765327695560254 - nodes in this community are weakly interconnected._