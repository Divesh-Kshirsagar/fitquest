# Implementation Plan: Offline Frontend Sprint

## 1. Local Database & Domain Persistence Layer
- [x] Create `UserProfileEntity`, `RunSessionEntity`, `DailyQuestEntity`, and `AchievementEntity`
- [x] Create DAOs: `UserDao`, `RunSessionDao`, `QuestDao`, `AchievementDao`
- [x] Update `FitQuestDatabase` to version 2 with all entities and DAOs
- [x] Implement `UserProfileRepository`, `RunSessionRepository`, `QuestRepository`, `AchievementRepository`
- [x] Wire repositories into Koin `AppModule.kt`

## 2. Onboarding & Profile Setup Flow
- [x] Build interactive Onboarding carousel (`OnboardingScreen.kt` with 3 feature slides)
- [x] Build Permission explainer step
- [x] Build Profile Setup step with avatar picker, codename input, daily step goal chips
- [x] Update `MainActivity.kt` router to check `isOnboardingCompleted` and route dynamically

## 3. Home Dashboard (`HomeTab.kt`)
- [x] Build circular daily activity card (steps, goal, calories, distance)
- [x] Build Territory Dominance banner (hex count, area covered km²)
- [x] Build Daily Quests list card with progress and XP reward display
- [x] Build Recent Sessions list with visual summaries
- [x] Connect with Room repository reactive flows

## 4. Enhanced Run & Post-Run Experience (`CurrentRunScreen.kt`)
- [x] Add live HUD metrics (formatted timer `HH:MM:SS`, distance km, live steps, current hex, session captured hexes)
- [x] Add Pause / Resume / Stop session controls
- [x] Build Post-Run Victory Dialog showing XP gain, captured hexes breakdown, calories, and persistence
- [x] Update `CaptureScreenModel.kt` to trigger level/XP gains, quest progress updates, and achievement unlocks upon run completion

## 5. Leaderboard & District Rankings (`LeaderboardTab.kt`)
- [x] Build District Tier progression card (Novice -> Scout -> Pioneer -> Sovereign -> Hex Master)
- [x] Build simulated Local Champions leaderboard table with active player ranking and score

## 6. Profile, Achievements & Settings (`ProfileTab.kt` & `AchievementsTab.kt`)
- [x] Build Profile Header with Avatar, Codename, Level, XP bar, Streak badge
- [x] Build Lifetime Stats matrix (Hexes, Steps, Distance, Calories, Longest Streak)
- [x] Build Achievement Badges tab (`AchievementsTab.kt`) with category filters and progress bars
- [x] Build Territory Vault list displaying owned hexes
- [x] Build Settings / Edit Profile Dialog with avatar picker and codename changes

## 7. Verification & Testing
- [x] Run `./gradlew :app:test` to ensure all tests pass (57 tasks executed successfully)
- [x] Verify clean app compilation and zero stub states
- [x] Update ledger and architecture decision records
