# Feature Sprint Requirements: Full Local Frontend Experience

## Branch Scope
This sprint focuses exclusively on completing the frontend application as a standalone, rich, and responsive gamified fitness game using local device persistence (Room DB + Preferences). Backend integration and the social Friends feature are explicitly out of scope for this sprint.

---

## 1. Onboarding & Identity Flow
- **R1.1 Feature Intro Pager:** 3 interactive onboarding slides explaining:
  1. *Walk & Explore:* Physical steps map to real-world movement.
  2. *Conquer Hexagons:* Uber H3 spatial grid partitions the world into capture zones.
  3. *Level Up & Defend:* Earn XP, unlock badges, and build daily streaks.
- **R1.2 Pre-Permission Explainer:** Clear visual cards explaining why Location (`ACCESS_FINE_LOCATION`) and Physical Activity (`ACTIVITY_RECOGNITION`) permissions are needed before triggering system dialogs.
- **R1.3 Profile Creation:** User inputs codename/username, selects an avatar (from preset runner avatars), and sets daily step goals (e.g., 6,000 / 8,000 / 10,000 / custom).
- **R1.4 Onboarding State Persistence:** Onboarding is only shown once; subsequent app launches route directly to the Main Hub.

---

## 2. Home Dashboard (`HomeTab.kt`)
- **R2.1 Daily Step & Activity Ring:** Dynamic progress arc tracking today's steps against the user's daily goal, displaying calculated active calories (~0.04 kcal/step) and distance (~0.75 m/step).
- **R2.2 Territory Dominance Card:** Total captured hexes count, today's newly claimed hexes, and total conquered area (in m² / km²).
- **R2.3 Daily Quests:** 3 auto-generated daily challenges (e.g. *"Walk 5,000 Steps"*, *"Capture 3 New Hexes"*, *"Complete a 15-Minute Walk"*), with live progress bars, XP rewards, and claimable state.
- **R2.4 Quick Start Capture CTA:** Prominent floating/featured button to launch `CurrentRunScreen`.
- **R2.5 Recent Sessions Carousel:** Visual history of recent run/walk sessions with timestamp, duration, steps, and conquered hexes.

---

## 3. Enhanced Run & Post-Run Experience (`CurrentRunScreen.kt`)
- **R3.1 Active Session HUD:**
  - Live session timer (HH:MM:SS format).
  - Real-time step counter & estimated distance (km).
  - Current Hex ID & defense score accumulation indicator.
  - Total session hexes conquered counter.
- **R3.2 Session Controls:** Toggle start, pause, resume, and finish.
- **R3.3 Post-Run Victory Modal / Summary Dialog:**
  - Shows total session steps, elapsed duration, distance (km), active calories burned, and total hexes captured.
  - Displays earned XP breakdown (e.g., +50 XP per new hex, +10 XP per defended hex, +1 XP per 100 steps).
  - Automatically records the session in local Room database and triggers profile XP/level updates.

---

## 4. Leaderboard & Territory Rankings (`LeaderboardTab.kt`)
- **R4.1 District / League Tiers:** Tier progression system based on total hexes conquered (e.g. *Novice Scout*, *District Pioneer*, *Urban Conqueror*, *Regional Sovereign*, *Hex Master*).
- **R4.2 Simulated Local Champions Leaderboard:** Local district ranking table with AI contender personas showing step counts and controlled territory, giving competitive single-player feedback.

---

## 5. Profile & Achievements Tab (`ProfileTab.kt`)
- **R5.1 Profile Header & Level Progress:** User avatar, codename, current level (e.g., Level 5), current XP, XP required for next level, and streak flame badge.
- **R5.2 Lifetime Stats Grid:** Total lifetime steps, total distance covered, total territories owned, total runs completed, best daily step record, longest streak.
- **R5.3 Territory Vault:** Interactive list of conquered hexes with coordinates and step investment.
- **R5.4 Achievement Badges:** 8+ unlockable badges across Conquest, Endurance, and Streak categories with progress trackers.
- **R5.5 Settings & Dev Tools Dialog:** Allow editing daily step goals, resetting local data for testing, and quick toggles for Dev Location/Step Simulators.

---

## 6. Local Offline Persistence Layer
- **R6.1 User Profile Entity & DAO:** Full profile state (username, avatar, goals, level, XP, streaks, onboarding state).
- **R6.2 Run Session Entity & DAO:** Historical run logs (duration, steps, distance, calories, captured hexes list, XP).
- **R6.3 Daily Quests Entity & DAO:** Local dynamic quests generated per calendar day with progress tracking.
- **R6.4 Achievement Entity & DAO:** Milestone tracking evaluated after each run session.
