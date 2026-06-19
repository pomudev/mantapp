# Mantapp Frontend Implementation Checklist

This checklist covers user-facing Android, Jetpack Compose, navigation, screen state, visualizations, copy, and frontend validation work.

## 0. Harness Review

- [x] Create `.harness/AGENTS.md`.
- [x] Create `.harness/PLAN.md`.
- [x] Split the original `.harness/TODOLIST.md` into `.harness/FRONTENDTODOLIST.md` and `.harness/BACKENDTODOLIST.md`.
- [x] Wait for user review.
- [x] Apply requested harness edits.
- [x] Do not implement app features before harness approval.

## 1. Frontend Baseline

- [x] Re-scan the workspace for existing source files.
- [x] Confirm whether the app should be created from scratch or imported from another location: create from scratch in this repo.
- [x] Confirm Android Gradle Plugin, Kotlin, Compose, and chart library versions with backend needs: AGP `9.2.1`, AGP built-in Kotlin support with Hilt-compatible Kotlin Compose plugin `2.3.21`, Jetpack Compose BOM `2024.06.00`, Vico Compose `1.16.1`.
- [x] Confirm package name and app display name: `com.mantapp.app`, `Mantapp`.
- [x] Confirm minimum SDK and target SDK: min SDK 26, target SDK 34.
- [x] Confirm whether authentication is local-only for MVP: local-only, no external auth or network-backed session provider.
- [x] Record user answers for unresolved frontend baseline decisions before creating app source files.

## 2. Android and Compose Foundation

- [x] Create or verify Gradle project structure with backend.
- [x] Start Android and Compose Foundation by verifying repository contents and local tooling availability.
- [x] Confirm current repository has no Android source files before scaffolding.
- [x] Confirm app module name before creating Gradle project structure: `:app`.
- [x] Confirm compile SDK before creating Android module: 34.
- [x] Confirm Gradle wrapper strategy because `gradle` is not available on PATH: Gradle wrapper configured for Gradle `9.4.1`.
- [x] Configure Kotlin.
- [x] Configure Jetpack Compose.
- [x] Add Compose navigation with compile SDK 34-compatible stable `androidx.navigation:navigation-compose:2.7.7`.
- [x] Add chart dependency: Vico Compose `1.16.1`.
- [x] Add frontend test dependencies.
- [x] Confirm frontend test dependency versions and test framework choices: JUnit 4, AndroidX Test, Espresso, Compose UI testing, MockK.
- [x] Create app theme and shared typography.
- [x] Create reusable form components.
- [x] Create reusable money input component.
- [x] Create reusable empty, loading, and error states.
- [x] Create reusable confirmation, success, and warning UI patterns.
- [x] Install and configure local JDK and Android SDK for Android verification.
- [x] Run Gradle verification after a JDK and Android SDK are available in the environment: `testDebugUnitTest` and `assembleDebug` pass locally.

## 3. Frontend Architecture

- [x] Define frontend package structure for UI screens, components, navigation, and ViewModels.
- [x] Add ViewModel state models.
- [x] Add navigation route definitions.
- [x] Establish dependency injection approach with backend if needed: Hilt `2.59.2` with AndroidX Hilt Navigation Compose `1.2.0`.
- [x] Define UI event contracts for form submission, proof actions, reward redemption, and settings updates.
- [x] Add Hilt application entry point and baseline DI module.
- [x] Verify Hilt-compatible Kotlin `2.3.21`, KSP `2.3.9`, and Compose plugin configuration.
- [x] Run Gradle verification after frontend architecture setup: `testDebugUnitTest` and `assembleDebug` pass locally.

## 4. User and Authentication Screens

- [ ] Build registration screen.
- [ ] Build login screen.
- [ ] Show local-only authentication messaging if MVP authentication is simulated.
- [ ] Route new users to onboarding.
- [ ] Route returning onboarded users to dashboard.
- [ ] Handle invalid login, empty form, loading, and success states.

## 5. Onboarding Flow

- [ ] Create onboarding navigation flow.
- [ ] Build monthly income question UI.
- [ ] Build employment status question UI.
- [ ] Build income stability question UI.
- [ ] Build debt status question UI.
- [ ] Build debt type question UI when debt exists.
- [ ] Build emergency savings status question UI.
- [ ] Build emergency savings coverage question UI.
- [ ] Build main financial goals question UI.
- [ ] Build short-term purchase goal question UI.
- [ ] Build risk tolerance question UI.
- [ ] Build budgeting approach preference question UI.
- [ ] Build upcoming major expenses question UI.
- [ ] Validate required answers before advancing.
- [ ] Show review step before completion if practical.
- [ ] Allow editing answers through Settings.

## 6. Income and Expense UI

- [ ] Build monthly salary form.
- [ ] Build expense category entry UI.
- [ ] Add rent or housing category.
- [ ] Add utilities category.
- [ ] Add groceries category.
- [ ] Add transportation category.
- [ ] Add insurance category.
- [ ] Add credit card minimum payment category.
- [ ] Add loan repayment category.
- [ ] Add phone and internet category.
- [ ] Add education expense category.
- [ ] Add subscriptions category.
- [ ] Add other necessary commitments category.
- [ ] Show total essential expenses.
- [ ] Show disposable income preview.
- [ ] Handle low or negative disposable income messaging.

## 7. Recommendation UI

- [ ] Build recommendation screen.
- [ ] Show allocation percentages.
- [ ] Show allocation amounts.
- [ ] Show deterministic rationale text.
- [ ] Show AI explanation when future backend integration provides it.
- [ ] Show educational disclaimer with recommendation output.
- [ ] Handle missing profile, missing income, and missing expense states.
- [ ] Provide action to continue to dashboard or progress tracking.

## 8. Dashboard

- [ ] Create dashboard screen.
- [ ] Show monthly salary.
- [ ] Show total essential expenses.
- [ ] Show disposable income.
- [ ] Show allocation recommendation summary.
- [ ] Show allocation chart.
- [ ] Show progress summary.
- [ ] Show points balance.
- [ ] Show next recommended action.
- [ ] Handle missing onboarding state.
- [ ] Handle missing income or expense data.
- [ ] Handle low or negative disposable income messaging.

## 9. Progress Tracking UI

- [ ] Create progress tracking screen.
- [ ] Build weekly check-in UI.
- [ ] Build savings action log UI.
- [ ] Build debt repayment action log UI.
- [ ] Build allocation completion log UI.
- [ ] Show progress logs linked to active recommendations.
- [ ] Show monthly completion status.
- [ ] Show streak candidates when available.

## 10. Simulated Proof Verification UI

- [ ] Build simulated proof upload or attachment UI.
- [ ] Show new proof submissions as `Pending`.
- [ ] Add local approval action for prototype verification.
- [ ] Add local rejection action for prototype verification.
- [ ] Show verification history.
- [ ] Show why points are unavailable while proof is pending.
- [ ] Show points awarded after approval.

## 11. Rewards UI

- [ ] Create reward catalogue screen.
- [ ] Display FamilyMart RM5 voucher.
- [ ] Display TGV Movie Ticket RM5 voucher.
- [ ] Display GSC Snack Combo voucher.
- [ ] Display Petronas RM5 voucher.
- [ ] Show point balance from ledger.
- [ ] Build simulated reward redemption flow.
- [ ] Show insufficient-points state.
- [ ] Show redeemed reward history if backend data is available.

## 12. Settings

- [ ] Replace or avoid a standalone Profile page in favor of Settings.
- [ ] Add basic user profile settings UI.
- [ ] Add financial profile settings UI.
- [ ] Add app preference settings UI.
- [ ] Add AI configuration settings or placeholder UI.
- [ ] Add educational disclaimer screen or section.
- [ ] Add data reset or local data management option if approved.

## 13. Charts and Visualization

- [ ] Choose Vico or MPAndroidChart with backend.
- [ ] Add allocation breakdown chart.
- [ ] Add progress chart.
- [ ] Add points or streak visualization if useful.
- [ ] Test chart rendering with empty, small, and large values.
- [ ] Verify chart labels and values remain readable on compact screens.

## 14. Frontend Quality and Testing

- [ ] Add basic ViewModel tests.
- [ ] Add UI state tests where practical.
- [ ] Test form validation states.
- [ ] Test navigation between core flows.
- [ ] Run build verification.
- [ ] Run frontend-relevant test suite.
- [ ] Review app copy for financial safety.
- [ ] Review UI for accessibility, readability, and responsive layout.

## 15. Frontend Documentation and Release Readiness

- [ ] Update README frontend setup once source exists.
- [ ] Document app navigation and core screens.
- [ ] Document current MVP UI limitations.
- [ ] Document simulated proof verification behavior from a user perspective.
- [ ] Update `.harness` files when frontend product direction changes.

## 16. GitHub Publishing Coordination

- [x] Wait for explicit user approval before publishing.
- [x] Confirm Git is available or configure Git path.
- [x] Confirm remote repository: `https://github.com/pomudev/mantapp`.
- [x] Check working tree status before committing.
- [x] Confirm commits are authored as the correct human contributor or another user-approved human maintainer identity, never Codex or another non-human identity.
- [x] Coordinate commit scope with backend work.
- [x] Push only after user gives the green light.

## 17. Checklist Maintenance

- [x] Add rule requiring both frontend and backend checklists to be updated after each completed sequence of tasks.
- [x] Review current frontend checklist status after harness publishing and attribution cleanup.
- [x] Update frontend checklist after starting Frontend Baseline and re-scanning the workspace.
- [x] Update frontend checklist after resolving Frontend Baseline decisions.
- [x] Update frontend checklist after starting Android and Compose Foundation and identifying blocking decisions.
- [x] Update frontend checklist after scaffolding Android and Compose foundation.
- [x] Update frontend checklist after adding reusable UI foundation components.
- [x] Update frontend checklist after local JDK/Android SDK setup and Gradle verification.
- [x] Update frontend checklist after Frontend Architecture setup with Hilt and navigation contracts.
- [ ] Update this checklist whenever frontend task status, scope, publishing state, or implementation progress changes.
