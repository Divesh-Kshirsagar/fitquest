# ADR 0001: Offline-First Standalone Mobile Frontend Architecture

## Context & Problem Statement
The FitQuest mobile client was previously a bare capture prototype where onboarding, hub dashboard, leaderboard, and profile screens were minimal stubs. The user requested a complete, self-contained, working frontend app where all state (user profile, onboarding status, run sessions, daily quests, and achievements) is persisted locally on the user's device, with the social friends feature scoped out of this sprint.

## Decisions

### 1. Local-First Room Persistence Expansion
- **Database (`FitQuestDatabase.kt`)**: Incremented database version to 2 with `fallbackToDestructiveMigration()` enabled for development flexibility.
- **Entities Added**:
  - `UserProfileEntity`: Stores codename, avatar, level, XP, daily goal, streak metrics, and onboarding state.
  - `RunSessionEntity`: Logs individual run sessions with start/end timestamps, duration, steps, distance, calories, hex list, and XP earned.
  - `DailyQuestEntity`: Generates daily operational goals (steps, hexes, endurance walks) per calendar day.
  - `AchievementEntity`: Manages milestone badges across Conquest, Endurance, and Streak categories.
- **Repositories**: Standard reactive repository pattern (`UserProfileRepository`, `RunSessionRepository`, `QuestRepository`, `AchievementRepository`) exposing Kotlin `Flow` streams to Compose UI.

### 2. Onboarding & Dynamic App Initialization
- **`OnboardingScreen.kt`**: Implemented a 4-step progressive onboarding experience (Concept explanation -> Territory conquest -> Pre-permission explainer -> Identity customization).
- **Router (`MainActivity.kt`)**: Dynamically queries `userProfileRepository.getProfile().isOnboardingCompleted`. First-time users route to onboarding; returning users launch straight into `MainHubScreen`.

### 3. Active Run Experience & Post-Run Summary Modal
- **Live Metrics**: `CurrentRunScreen.kt` displays a real-time HUD with elapsed time, distance in kilometers ($steps \times 0.75\text{m}$), active calories ($steps \times 0.04\text{kcal}$), current hex ID, and newly captured hex count.
- **Controls**: Full support for Start, Pause, Resume, and Stop & Finish.
- **Post-Run Victory Modal**: Displays session recap, XP breakdown, and unlocked achievements upon session completion. Automatically commits session records to the Room database off the main UI thread.

### 4. Navigation Architecture & Tabs
- Repurposed the stubbed `FriendsTab` into a full-featured `AchievementsTab` ("Trophies") displaying category filters (Conquest, Endurance, Streak) and live progress bars.
- Enhanced `LeaderboardTab` with a District Tier progression ladder and local contender simulation comparing user progress against local district runners.
- Completed `ProfileTab` with career statistics matrix, daily goal setting, avatar selection, and territory vault.

## Consequences & Follow-ups
- The application is completely functional and testable on real devices and emulators without requiring a live Supabase backend connection.
- In future sprints, an outbox sync worker can read from `RunSessionEntity` to push historical sessions to the FastAPI backend when network connectivity is restored.
