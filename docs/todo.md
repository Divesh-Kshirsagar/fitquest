# Implementation Todos

## Current Phase: Grounding & Baseline Health
- [x] Audit mobile codebase (`apps/app`) and backend API (`apps/api`)
- [x] Fix unit test compilation failure in `HexGeoJsonMapperTest.kt` (`getHexesInRadius`, `toGeoJsonString`)
- [x] Fix Gradle command target in `apps/app/README.md` (`:fitquest:test` -> `:app:test`)
- [x] Research competitive turf apps and add references into root `README.md`
- [x] Establish `.agent-context.md` with durable architecture requirements
- [x] Initialize `docs/agent_ledger.md` with standard execution entry
- [x] Record initial architecture decision in `docs/docs/architecture/0001-project-grounding-and-territory-engine.md`

## Next Up
- [ ] Implement offline Room outbox queue in `CaptureScreenModel.kt` for run sync payloads
- [ ] Connect Supabase JWT auth store to OkHttp interceptor in `AppModule.kt`
- [ ] Add velocity check / anti-cheat filter in `HexCaptureEngine.kt`
- [ ] Build out real screens for `LeaderboardTab.kt`, `FriendsTab.kt`, `ProfileTab.kt`
- [ ] Replace H3 `.so` files with 16 KB page-size aligned builds
