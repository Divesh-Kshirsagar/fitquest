

# TerraMove â€” Spatial Gamification & AI Coach

## 1. Overview
TerraMove is a spatial gamification platform that converts real-world movement into digital territory ownership on an H3 hex grid. An AI coaching agent analyzes movement patterns, recommends territories to capture, celebrates achievements, and provides competitive intelligence â€” all while enforcing strict privacy-preserving principles (anonymous by default, no raw GPS exposure, opt-in sharing). Users track their territory portfolio, view leaderboards, and receive personalized insights to stay motivated.

## 2. User Stories
- As a player, I want to capture hex territories by physically moving through them so that I build a digital territory portfolio.
- As a player, I want an AI coach to analyze my movement patterns and recommend new territories so that I stay engaged and strategic.
- As a player, I want to see achievement celebrations and competitive intelligence so that I'm motivated to keep expanding.
- As a player, I want my exact GPS coordinates to never be exposed and all data to be anonymous by default so that my privacy is protected.
- As a player, I want to view my territory history, stats, and leaderboard position so that I can track progress over time.

## 3.a. Agent Architecture

**Pattern:** Multiple Independent Agents

**Reasoning:** The app has two distinct LLM tasks triggered at different times â€” real-time coaching/recommendations during or after a movement session, and on-demand pattern analysis/competitive intelligence review. These are naturally separated by user intent and timing, so independent agents with separate CTAs serve best.

**Agent Flow:**
User logs in â†’ lands on Dashboard showing territory map and stats (pulled from MongoDB). User completes a movement session (territory data saved to DB by frontend). User clicks \"Get Coaching\" â†’ AI Coach Agent analyzes recent session data, provides real-time recommendations, celebrates captures, and suggests next territories. Separately, user navigates to Insights tab and clicks \"Analyze Patterns\" â†’ Movement Analyst Agent reviews historical movement data, provides pattern analysis, competitive intelligence, and strategic recommendations. Both agents receive context from MongoDB records via the user message (frontend fetches user data, passes to agent).

**Data Sources Detected:** 0 â€” No external data connectors or knowledge bases. Agents operate on user-provided movement context passed from the frontend.

**Data Source Assumption:** No specific external data source was provided. Agents reason over user movement/session data passed from MongoDB via the frontend. If an external dataset (e.g., city maps, POI databases) is available, it can be added as a Data Connector later.

**Agents Table:**
| Agent Type | Agent Name | Description | Tools/Data Sources | Trigger | Provider | Model | Temperature | Top_p |
|------------|------------|-------------|-------------------|---------|----------|-------|-------------|-------|
| Independent | AI Coach Agent | Analyzes the user's latest movement session, provides real-time coaching insights, territory capture celebrations, recommends next territories to explore, and gives motivational feedback â€” all without exposing raw GPS coordinates | N/A | \"Get Coaching\" on Dashboard | OpenAI | gpt-5.2 | 0.7 | 0.95 |
| Independent | Movement Analyst Agent | Reviews historical movement patterns across multiple sessions, provides strategic pattern analysis, competitive intelligence against anonymized leaderboard data, and long-term territory expansion strategies | N/A | \"Analyze Patterns\" on Insights Screen | OpenAI | claude-sonnet-4-5 | 0.5 | 0.95 |

**Workflow Visualization:**
The Input Node is positioned at the far left. The AI Coach Agent connects to the right of the Input Node at the same vertical level. The Movement Analyst Agent connects to the right of the AI Coach Agent at the same vertical level. Both are independent agents with separate triggers â€” the horizontal layout represents the two distinct entry points, not a sequential dependency.

**Connection Summary:**
- Input â†’ AI Coach Agent: Right
- Input â†’ Movement Analyst Agent: Right

## 3.g. Database Configuration

**Database:** MongoDB (Built-in Lyzr Studio database)

**User Management:** Required â€” email/password signup and login, all screens gated behind authentication.

| Collection / Entity | Purpose | Key Fields |
|---------------------|---------|------------|
| users | User accounts and profile data | id, email, password_hash, display_name, created_at, total_hexes_captured, current_streak |
| territories | Hex territories captured by users | id, user_id, h3_index, captured_at, session_id, privacy_zone (no raw GPS stored) |
| sessions | Movement sessions with aggregated stats | id, user_id, started_at, ended_at, hexes_captured, distance_approx, duration |
| achievements | Unlocked achievements and milestones | id, user_id, achievement_type, unlocked_at, description |
| leaderboard | Anonymized ranking snapshots | id, anonymous_id, total_hexes, rank, updated_at |

**Roles:**
| Role | Access Level |
|------|-------------|
| user | Full access to own territory, sessions, achievements, coaching, and insights |

**Authentication Flow:** Standard email/password signup and login. All app screens require authentication. User data is scoped â€” each user sees only their own territories and stats.

## 4. User Flow
```
1. User lands on Login Screen â†’ enters credentials or clicks \"Sign Up\"
2. New user â†’ Sign Up form (display name, email, password) â†’ account created â†’ redirected to Dashboard
3. Returning user â†’ logs in â†’ enters Dashboard
4. Dashboard displays: territory hex map (user's captured hexes), session summary cards, streak counter, recent achievements
5. User completes a movement session (frontend captures H3 hex data via adaptive GPS, saves session + territories to MongoDB â€” no raw coords stored)
6. User clicks \"Get Coaching\" â†’ AI Coach Agent receives latest session context â†’ returns coaching insights, celebrations, territory recommendations in a chat panel
7. User navigates to Insights tab â†’ views historical stats â†’ clicks \"Analyze Patterns\" â†’ Movement Analyst Agent receives multi-session history â†’ returns pattern analysis, competitive intelligence, strategic advice
8. User views Leaderboard (anonymized rankings from DB)
9. User views Achievements gallery showing unlocked milestones
```

## 5. Integrations Required

No out-of-the-box integrations required for this app.

> **Note:** The H3 hex grid computation, adaptive GPS tracking, and optional blockchain verification are frontend/backend engineering tasks handled outside the agent layer. The agents focus on LLM-powered coaching and analysis only.

## 6. UI/UX Specification

[SELECTED_THEME: emerald-dark]

### App Structure
Left sidebar navigation (collapsible on mobile) with sections: Dashboard, Insights, Achievements, Leaderboard, Settings. Top header with app logo, user avatar, and streak badge. Main content area adapts per section.

### Design System
**Components:** Hex-grid map card, session summary cards, stat badges, achievement tiles, chat panel for agent output, leaderboard table rows, progress bars, streak counters.
**Visual Hierarchy:** 8pt grid spacing. Large bold headings for section titles, medium weight for stat numbers, subtle secondary text for labels and timestamps. Hex map is the visual anchor on Dashboard.
**Information Density:** Dense but organized â€” Dashboard uses a grid layout with map taking 60% width and stat cards filling the remaining column. Minimal empty space, every pixel serves a purpose.

### Screens

#### Screen 0: Login
**Purpose:** Authenticate existing users.
**Layout:** Centered card form over a subtle animated hex-grid background.
**Components:** Email input, password input, \"Log In\" button, \"Sign Up\" link, error toast for invalid credentials.

#### Screen 0b: Sign Up
**Purpose:** Create new player accounts.
**Layout:** Centered card form.
**Components:** Display name, email, password fields, \"Create Account\" button, \"Already have an account?\" link, field validation states.

#### Screen 1: Dashboard
**Purpose:** Central hub â€” view territory map, recent session, and trigger coaching.
**Layout:** Two-column grid â€” left (60%) hex territory map visualization, right (40%) stacked session summary card + streak counter + recent achievements mini-list.
**Components:**
- Hex Map Card: Visual grid of captured/uncaptured hexes (no raw GPS shown, only H3 indices rendered)
- Session Summary Card: Last session stats (hexes captured, duration, distance approx)
- \"Get Coaching\" CTA button â†’ opens right-side chat panel with AI Coach Agent response
- Streak Badge: Current consecutive-day streak
- Quick Achievements: Last 3 unlocked milestones

#### Screen 2: Insights
**Purpose:** Deep-dive into historical patterns and competitive strategy.
**Layout:** Top stats row (total hexes, sessions, avg per session) + main content area for agent output.
**Components:**
- Stats row: Total hexes captured, total sessions, average hexes/session, longest streak
- \"Analyze Patterns\" CTA button â†’ triggers Movement Analyst Agent, displays strategic analysis in an expandable results panel below
- Historical session list (sortable by date, hexes captured)
- Pattern visualization placeholder (chart area for session trends)

#### Screen 3: Achievements
**Purpose:** View all earned and locked milestones.
**Layout:** Grid of achievement tiles.
**Components:** Achievement tile (icon, title, description, unlock date or locked state), progress indicators for partially complete achievements, celebration animation on newly unlocked items.

#### Screen 4: Leaderboard
**Purpose:** Anonymized competitive ranking.
**Layout:** Single-column table.
**Components:** Rank number, anonymous display name, total hexes, badge tier. Current user's row highlighted. Privacy notice banner (\"All rankings are anonymized\").

### Component Specifications
**Input Components:** Email/password text fields with validation, chat input for follow-up coaching questions.
**Display Components:** Hex map canvas, stat cards with large numbers, achievement grid tiles, leaderboard table rows, agent response panels with markdown rendering.
**Action Components:** Primary CTA buttons (\"Get Coaching\", \"Analyze Patterns\"), secondary nav items, icon buttons for settings/profile.

### Production-Ready Requirements
- Skeleton loaders on Dashboard while map and stats load
- Empty states: \"No sessions yet â€” start moving to capture your first hex!\" with illustration
- Error handling: Toast notifications for failed agent calls with retry action
- Smooth transitions between nav sections, celebration micro-animations on achievement unlock
- Agent response panels show typing indicator while processing

### Complete User Journey
User opens app â†’ Login screen â†’ authenticates â†’ Dashboard loads with hex map and stats â†’ completes a real-world session (data saved by frontend) â†’ refreshes Dashboard â†’ sees updated map â†’ clicks \"Get Coaching\" â†’ chat panel slides in with AI Coach's insights and next-territory recommendations â†’ navigates to Insights â†’ clicks \"Analyze Patterns\" â†’ receives strategic analysis â†’ checks Achievements for new unlocks â†’ views Leaderboard for competitive standing