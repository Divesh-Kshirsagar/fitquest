# FitQuest API Backend Documentation

This document outlines the architecture, data models, and API structure for the FitQuest backend. The backend is built using **FastAPI**, **SQLModel**, and **Supabase** (PostgreSQL + Auth), optimized for a domain-driven design and high-performance geospatial queries.

## 🚀 Tech Stack
* **Framework:** FastAPI
* **ORM:** SQLModel (Pydantic + SQLAlchemy)
* **Database:** PostgreSQL (Hosted on Supabase with PostGIS & H3-pg extensions)
* **Async Engine:** asyncpg
* **Authentication:** Supabase JWT Verification
* **Migrations:** Alembic

## 📂 Architecture: Domain-Driven Design
The project ditches the traditional "types-based" folder structure (e.g., all models in one folder) for a **Feature-Based (Module-Wise)** approach. Each domain completely encapsulates its own logic, schemas, models, and endpoints.

```text
apps/api/
├── app/
│   ├── main.py             # Main FastAPI app and router mounting
│   ├── core/               # Global settings, config, security, db connection
│   ├── api/                # Global API routing & dependencies
│   └── modules/            # Domain Modules (The core logic)
│       ├── users/
│       ├── map/
│       ├── runs/
│       └── quests/
```

## 🧩 Modules Breakdown

### 1. Users Module (`app/modules/users`)
Handles core identity, profile stats, streaks, and a self-referencing many-to-many relationship for friends.
* **Models:** `User`, `Friendship`
* **Stats:** Tracks lifetime steps and hexes captured.
* **Design Choice:** User rows are strictly created via **Supabase Postgres Triggers** when a user signs up via Supabase Auth. The API only reads and updates profile stats.
* **DTOs:** Flattens relationships so the Android frontend doesn't need to parse complex graph data (`UserProfileResponse`, `FriendListResponse`).

### 2. Map Module (`app/modules/map`)
The engine of the multiplayer turf war. Engineered to keep MapLibre viewport queries lightning fast.
* **Models:** `HexOwnership` (Uses H3 string as the Primary Key).
* **Endpoints:** Zoom-aware viewport queries.
    * **High Zoom (>= 14):** Returns exact `HexDetailResponse` (Street level).
    * **Low Zoom (< 14):** Uses raw SQL + H3 functions (`h3_to_parent`) to aggregate data into `HeatmapResponse` grids (City/State level).

### 3. Runs (Capture Sync) Module (`app/modules/runs`)
Handles the ingestion of completed runs from the mobile frontend.
* **DTOs:** `RunSyncPayload` explicitly mirrors the Android `Map<String, Int>` (`hex_id` to `steps` mapping).
* **Logic:** The sync engine iterates through the payload, performs DB UPSERTs on the `HexOwnership` table, calculates defensive scores, and returns a gamified `RunSyncSummary` (stolen hexes, newly captured hexes, XP).

### 4. Quests Module (`app/modules/quests`)
A specialized system for dynamic challenges.
* **Models:** `Quest` (System-wide definitions) and `UserQuest` (Individual tracking).
* **Logic:** Allows assigning tasks like "Take 5000 steps today". 

## 🔐 Auth & Database Connection
* **Auth:** Endpoints are protected by a `get_current_user` dependency that reads the incoming `Bearer` token and resolves it securely against the Supabase `auth.users` instance.
* **Async Engine:** The backend leverages SQLAlchemy's async engine (`postgresql+asyncpg`) to handle high concurrency, especially important for handling multiple players streaming bounding boxes over the map interface.

## 🛠️ Typical Developer Workflow
1. **Adding a Feature:** Create a new folder under `app/modules/`.
2. **Define the Database Table:** Create `models.py`.
3. **Define the Input/Output JSON:** Create `schemas.py`.
4. **Write Business Logic:** Create `service.py` functions (No HTTP logic here).
5. **Expose Endpoints:** Tie the schemas and services together in `router.py`.
6. **Mount:** Add the router to `app/api/router.py`.
