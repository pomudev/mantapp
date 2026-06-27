# Mantapp High-Level Roadmap

## Roadmap Status

This roadmap now reflects an Android app source baseline in the standard `:app` module. The app foundation, Hilt architecture setup, navigation scaffolding, simulated local authentication screens, onboarding UI, income and expense UI, recommendation UI, dashboard UI, progress tracking UI, and a local recommendation coordinator/fallback path are implemented; Room persistence, persisted auth/session state, proof verification, rewards, settings, production charting, and tests are still pending.

## Phase 0: Harness and Project Alignment

Define the working project memory before implementation starts.

Deliverables:

- `.harness/AGENTS.md` with project rules and product specification.
- `.harness/PLAN.md` with this high-level roadmap.
- `.harness/FRONTENDTODOLIST.md` with the detailed frontend implementation checklist.
- `.harness/BACKENDTODOLIST.md` with the detailed backend implementation checklist.
- User review and approval before any application implementation.

Exit criteria:

- User approves the harness files or requests edits.

## Phase 1: App Foundation

Create or align the Android/Kotlin app foundation.

Goals:

- Kotlin-based app structure.
- Jetpack Compose UI foundation.
- New Android app created from scratch in this repository.
- Package name `com.mantapp.app` and app display name `Mantapp`.
- Minimum SDK 26 and target SDK 34.
- Android Gradle Plugin `9.2.1`, AGP built-in Kotlin support with Hilt-compatible Kotlin Compose plugin `2.3.21`, Jetpack Compose BOM `2024.06.00`, and Vico Compose `1.16.1`.
- KSP `2.3.9`, Room `2.6.1`, JUnit 4, AndroidX Test, Espresso, Compose UI testing, and MockK.
- Hilt `2.59.2` and AndroidX Hilt Navigation Compose `1.2.0` provide the DI foundation.
- Local-only MVP authentication with offline Room-backed session and profile persistence.
- MVVM architecture.
- Navigation setup using compile SDK 34-compatible stable Navigation Compose `2.7.7`.
- Room database dependencies and baseline setup.
- Shared design tokens and reusable UI components.

Exit criteria:

- App builds locally with the local JDK 17 and Android SDK setup.
- Main navigation destinations exist.
- Empty or placeholder screens are connected.
- Room database can initialize.

## Phase 2: Data Model and Local Persistence

Implement the local data layer needed for the MVP.

Goals:

- Room entities for users, financial profiles, expenses, recommendations, progress logs, rewards, and point transactions.
- DAOs and repositories.
- Seed data for simulated reward catalogue.
- Migration strategy.

Exit criteria:

- Core data can be created, read, updated, and deleted locally.
- Reward catalogue is available without network access.
- Repository tests cover critical database behavior.

## Phase 3: Onboarding and Financial Profile

Build the required questionnaire and profile setup flow.

Goals:

- Use the current simulated local authentication screens as the entry point until Room-backed auth/session persistence is implemented.
- Collect income stability, employment status, debt status, debt types, emergency savings, goals, risk preference, budgeting preference, and upcoming major expenses.
- Prevent users from reaching financial recommendations before required onboarding is complete.
- Store profile answers locally.

Exit criteria:

- A new user can complete onboarding.
- Saved profile data is visible and editable through Settings.
- Required-field and money-input validation exists.

## Phase 4: Income, Expense, and Disposable Income Engine

Implement salary and essential expense entry.

Goals:

- Monthly salary input.
- Essential expense categories: housing, utilities, groceries, transportation, insurance, debt minimums, loan repayments, phone or internet, education, subscriptions, and other commitments.
- Disposable income calculation.
- Low or negative disposable income handling.
- Treat RM 1,500 or less as low monthly disposable income for a single-person profile, and scale that threshold by household size in future household-aware implementations.

Exit criteria:

- Users can enter and revise monthly financial data.
- Disposable income updates reliably.
- Calculation logic is unit tested.

## Phase 5: Recommendation Engine

Implement recommendations in two layers: rule-based guidance and AI final judgment.

Goals:

- Rule-based allocation guidance engine for priority hints, baseline ranges, safety constraints, and offline fallback.
- AI recommendation layer that makes the final allocation judgment once explicitly approved and enabled.
- Explainable allocation percentages and amounts.
- Recommendation persistence.
- LLM-ready interface for personalized allocation judgment and explanations.
- Educational disclaimer shown with recommendation output.

Exit criteria:

- Recommendations are generated from profile, income, expenses, debt state, rule guidance, and, when enabled, AI judgment.
- Rule-guidance outcomes are unit tested.
- LLM integration is safely disabled by default until explicitly approved.
- When AI is disabled, local rule guidance can produce provisional fallback recommendations.

## Phase 6: Dashboard and Visualization

Create the primary user-facing finance dashboard.

Goals:

- Salary, expenses, and disposable income summary.
- Allocation breakdown chart using Vico or MPAndroidChart.
- Progress metrics.
- Points balance.
- Next recommended actions.

Exit criteria:

- Dashboard gives users a clear monthly status at a glance.
- Charts render correctly.
- Empty, loading, and missing-data states are handled.

## Phase 7: Progress Tracking and Simulated Proof Verification

Build the plan-following workflow.

Goals:

- Weekly check-ins.
- Savings, debt repayment, and allocation action logs.
- Simulated proof upload or proof reference.
- Pending, approved, and rejected verification states.

Exit criteria:

- Users can log progress against a recommendation.
- Proof records can be marked pending, approved, or rejected.
- Approved actions can trigger point awards.

## Phase 8: Rewards and Points

Implement the gamified reward system.

Goals:

- Point ledger.
- Points earned for approved financial actions.
- Points redeemed for simulated rewards.
- Reward catalogue.
- Streak and milestone support where practical.

Exit criteria:

- Points cannot be awarded twice for the same approved action.
- Redemptions fail cleanly when points are insufficient.
- Point balance is derived from point transactions.

## Phase 9: Settings and App Preferences

Replace the Profile page concept with a robust Settings page.

Goals:

- Profile editing.
- Financial profile editing.
- AI configuration placeholder or controls.
- App preferences.
- Disclaimer and educational safety text.

Exit criteria:

- Users can manage stored profile and preference data.
- Settings does not duplicate onboarding unnecessarily.
- AI configuration is clearly optional and future-ready.

## Phase 10: Quality, Review, and Publishing

Prepare for review and eventual GitHub publication after user approval.

Goals:

- Build verification.
- Unit tests for rules and persistence.
- UI sanity checks for main flows.
- Documentation updates.
- Git commits attributed to the correct human contributor or maintainer, never Codex or other assistant/tool identities.
- Push to `pomudev/mantapp` only after the user gives explicit approval.

Exit criteria:

- User approves implementation state.
- Repository can be pushed to GitHub.
- GitHub contributors reflect the correct human contributor or maintainer, not Codex.
- No unresolved high-risk issues remain.
