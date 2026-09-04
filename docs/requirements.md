# FitQuest Requirements & Acceptance Criteria

## Project Grounding Acceptance Criteria

- [x] **Repository Audit & Documentation**: Comprehensive analysis of mobile app (`apps/app`) and backend API (`apps/api`) architectures.
- [x] **H3 Hexagonal Engine Understanding**: MapLibre integration, k=2 neighborhood rendering, GeoJSON generation off-main-thread, Resolution 10 (dev) vs Resolution 9 (prod).
- [x] **Hardware & Dev Sensor Orchestration**: Fused location + hardware step counter (`TYPE_STEP_COUNTER`) paired with toggleable `DevLocationSimulator` and `DevStepSimulator`.
- [x] **Database & State Synchronization**: Android Room local persistence synchronized via `POST /api/v1/runs/sync` to FastAPI + Supabase SQLModel backend.
- [x] **Unit Testing Validation**: Gradle `:app:test` passing cleanly on local Android SDK.
- [x] **Competitive & Open-Source Research**: Industry benchmark analysis (Run An Empire, Turf, INTVL, Stride, StepEarth, Strava Local Legends) and technical references documented in `README.md`.

## Active Feature Requirements (Upcoming Sprints)

1. **Anti-Cheat & Fraud Protection**:
   - Reject step accumulation if movement velocity exceeds 25 km/h (prevent driving/cycling abuse).
   - Validate realistic step cadence ($1.0 \text{ to } 4.0 \text{ steps/sec}$).
2. **Offline Sync Outbox Pattern**:
   - Store pending run sync payloads in Room database when offline.
   - Automatically retry upload with exponential backoff upon network restoration.
3. **Territory Depreciation / Season Resets**:
   - Implement score decay (e.g. 5-10% weekly) or monthly round resets on the backend to avoid stale monopolies.
4. **Android 15 16 KB Page Alignment**:
   - Verify or rebuild bundled native H3 binaries (`libh3-java.so`) for 16 KB ELF load alignment.
