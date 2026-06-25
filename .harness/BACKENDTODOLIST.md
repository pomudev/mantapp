# Mantapp Backend Implementation Checklist

This checklist covers local data, Room persistence, domain logic, recommendation rules, reward logic, proof verification state, testing, and future service boundaries.

## 0. Harness Review

- [x] Create `.harness/AGENTS.md`.
- [x] Create `.harness/PLAN.md`.
- [x] Split the original `.harness/TODOLIST.md` into `.harness/FRONTENDTODOLIST.md` and `.harness/BACKENDTODOLIST.md`.
- [x] Wait for user review.
- [x] Apply requested harness edits.
- [x] Do not implement app features before harness approval.

## 1. Backend Baseline

- [x] Re-scan the workspace for existing source files.
- [x] Confirm whether the app should be created from scratch or imported from another location: create from scratch in this repo.
- [x] Confirm Android Gradle Plugin, Kotlin, Room, and test dependency versions with frontend needs: AGP `9.2.1`, AGP built-in Kotlin support with Hilt-compatible Kotlin Compose plugin `2.3.21`, KSP `2.3.9`, Room `2.6.1`, JUnit 4, AndroidX Test, Espresso, Compose UI testing, MockK.
- [x] Confirm package name and app display name: `com.mantapp.app`, `Mantapp`.
- [x] Confirm minimum SDK and target SDK: min SDK 26, target SDK 34.
- [x] Confirm whether authentication is local-only for MVP: local-only, persist session and financial profiles offline using Room.

## 2. Project and Dependency Foundation

- [x] Create or verify Gradle project structure with frontend.
- [x] Review backend-impacting decisions while starting Android and Compose Foundation.
- [x] Confirm Gradle wrapper strategy because `gradle` is not available on PATH: Gradle wrapper configured for Gradle `9.4.1`.
- [x] Confirm compile SDK before creating Android module: 34.
- [x] Configure Kotlin.
- [x] Add Room dependencies.
- [x] Confirm Room dependency version before adding Room: `2.6.1`.
- [x] Add backend test dependencies.
- [x] Confirm backend test dependency versions and test framework choices: JUnit 4, AndroidX Test, Espresso, Compose UI testing, MockK.
- [x] Establish dependency injection approach if needed: Hilt `2.59.2` with AndroidX Hilt Navigation Compose `1.2.0`.
- [ ] Define package structure for data, domain, repositories, and services.
- [x] Install and configure local JDK and Android SDK for Android verification.
- [x] Run Gradle verification after a JDK and Android SDK are available in the environment: `testDebugUnitTest` and `assembleDebug` pass locally.

## 3. Architecture Setup

- [x] Define initial package boundaries shared with frontend: `di`, `ui/navigation`, `ui/state`, `ui/event`, and `viewmodel`.
- [ ] Define `data` package boundaries.
- [ ] Define `domain` package boundaries.
- [ ] Define repository interfaces.
- [ ] Add repository implementations.
- [ ] Add use-case or service classes for business logic.
- [x] Define initial UI contracts consumed by ViewModels.

## 4. Room Database

- [ ] Create `MantappDatabase`.
- [ ] Create `UserEntity`.
- [ ] Create `FinancialProfileEntity`.
- [ ] Create `ExpenseEntity`.
- [ ] Create `RecommendationEntity`.
- [ ] Create `ProgressLogEntity`.
- [ ] Create `RewardEntity`.
- [ ] Create `PointTransactionEntity`.
- [ ] Create DAOs for each entity.
- [ ] Create repository implementations.
- [ ] Seed simulated reward catalogue.
- [ ] Add database migrations once schema evolves.
- [ ] Add persistence tests for core entities.

## 5. User and Authentication MVP

- [x] Confirm frontend auth screens are simulated local-only until Room-backed auth persistence is implemented.
- [ ] Decide local-only account model for MVP.
- [ ] Define user registration data model.
- [ ] Define login/session data model.
- [ ] Store active user state locally.
- [ ] Persist onboarding completion state.
- [ ] Expose active user and onboarding status to frontend.

## 6. Financial Profile Persistence

- [ ] Store monthly income from onboarding.
- [ ] Store employment status.
- [ ] Store income stability.
- [ ] Store debt status.
- [ ] Store debt type when debt exists.
- [ ] Store emergency savings status.
- [ ] Store emergency savings coverage in months.
- [ ] Store main financial goals.
- [ ] Store short-term purchase goal.
- [ ] Store risk tolerance.
- [ ] Store budgeting approach preference.
- [ ] Store upcoming major expenses.
- [ ] Validate required answers.
- [ ] Persist onboarding answers.
- [ ] Add onboarding completion flag.
- [ ] Expose update methods for Settings.

## 7. Income and Expense Module

- [ ] Persist monthly salary.
- [ ] Persist expense category entries.
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
- [ ] Calculate total essential expenses.
- [ ] Calculate disposable income.
- [ ] Treat RM 1,500 or less as low monthly disposable income for a single-person profile.
- [ ] Scale the low disposable income threshold by household size when household-member inputs exist.
- [ ] Persist monthly income and expense data.
- [ ] Add tests for disposable income calculation.

## 8. Rule-Based Recommendation Engine

- [ ] Create recommendation domain models.
- [ ] Implement disposable income input validation.
- [ ] Implement no-emergency-fund allocation rule.
- [ ] Implement high-interest or credit-card debt rule.
- [ ] Implement stable-finances rule.
- [ ] Implement low-disposable-income rule.
- [ ] Implement short-term-goal adjustment.
- [ ] Ensure flexible spending remains realistic.
- [ ] Generate allocation amounts from percentages.
- [ ] Generate deterministic rationale text.
- [ ] Persist generated recommendations.
- [ ] Add tests for all rule branches.
- [ ] Provide educational disclaimer content to frontend.

## 9. LLM-Ready Recommendation Layer

- [ ] Define `RecommendationExplanationProvider` interface.
- [ ] Create local fallback explanation provider.
- [ ] Create placeholder AI configuration model.
- [ ] Keep actual network or LLM calls disabled until approved.
- [ ] Ensure rule-based allocations remain authoritative.
- [ ] Document future LLM integration requirements.

## 10. Progress Tracking Logic

- [ ] Support weekly check-ins.
- [ ] Support savings action logs.
- [ ] Support debt repayment action logs.
- [ ] Support allocation completion logs.
- [ ] Link progress logs to active recommendations.
- [ ] Calculate monthly completion status.
- [ ] Track streak candidates.
- [ ] Persist progress logs.

## 11. Simulated Proof Verification Logic

- [ ] Add proof reference field to progress logs.
- [ ] Set new proof submissions to `Pending`.
- [ ] Add local approval action for prototype verification.
- [ ] Add local rejection action for prototype verification.
- [ ] Prevent point awards while proof is pending.
- [ ] Award points only when proof is approved.
- [ ] Persist verification history.

## 12. Rewards and Points

- [ ] Create point calculation rules.
- [ ] Award 50 points for completed monthly financial profile.
- [ ] Award 100 points for approved savings recommendation.
- [ ] Award 100 points for approved debt repayment target.
- [ ] Award 25 points for weekly progress check-in.
- [ ] Award 300 points for 3-month savings streak.
- [ ] Award 75 points for reduced unnecessary spending.
- [ ] Prevent duplicate point awards.
- [ ] Add FamilyMart RM5 voucher seed data.
- [ ] Add TGV Movie Ticket RM5 voucher seed data.
- [ ] Add GSC Snack Combo voucher seed data.
- [ ] Add Petronas RM5 voucher seed data.
- [ ] Implement simulated reward redemption.
- [ ] Deduct points through point transactions.
- [ ] Calculate point balance from ledger.
- [ ] Add tests for point awarding and redemption.

## 13. Settings Data

- [ ] Support basic user profile updates.
- [ ] Support financial profile updates.
- [ ] Support app preference updates.
- [ ] Support AI configuration settings or placeholder data.
- [ ] Provide educational disclaimer text.
- [ ] Add data reset or local data management behavior if approved.

## 14. Backend Quality and Testing

- [ ] Add unit tests for disposable income calculation.
- [ ] Add unit tests for allocation rules.
- [ ] Add unit tests for low, at-threshold, and negative disposable income.
- [ ] Add unit tests for point awarding.
- [ ] Add unit tests for redemption rules.
- [ ] Add Room DAO tests.
- [ ] Run build verification.
- [ ] Run backend-relevant test suite.
- [ ] Review business logic for financial safety.

## 15. Backend Documentation and Release Readiness

- [ ] Update README backend setup once source exists.
- [ ] Document data model and local persistence.
- [ ] Document current MVP limitations.
- [ ] Document simulated proof verification behavior from a data perspective.
- [ ] Document that LLM integration is future-ready but disabled unless implemented.
- [ ] Update `.harness` files when backend product direction changes.

## 16. GitHub Publishing Coordination

- [x] Wait for explicit user approval before publishing.
- [x] Confirm Git is available or configure Git path.
- [x] Confirm remote repository: `https://github.com/pomudev/mantapp`.
- [x] Check working tree status before committing.
- [x] Confirm commits are authored as the correct human contributor or another user-approved human maintainer identity, never Codex or another non-human identity.
- [x] Coordinate commit scope with frontend work.
- [x] Push only after user gives the green light.

## 17. Checklist Maintenance

- [x] Add rule requiring both frontend and backend checklists to be updated after each completed sequence of tasks.
- [x] Review current backend checklist status after harness publishing and attribution cleanup.
- [x] Review backend checklist during Frontend Baseline start; no backend implementation status changed.
- [x] Update backend checklist with baseline decisions that affect backend scope.
- [x] Update backend checklist after starting Android and Compose Foundation and identifying backend-impacting decisions.
- [x] Update backend checklist after scaffolding Android and Compose foundation.
- [x] Review backend checklist after reusable frontend UI components; no backend implementation status changed.
- [x] Update backend checklist after local JDK/Android SDK setup and Gradle verification.
- [x] Update backend checklist after Hilt DI decision and shared architecture contracts.
- [x] Review backend checklist after User and Authentication Screens setup; Room-backed auth persistence remains pending.
- [x] Review backend checklist after current source-state update; Room/data/auth persistence remains pending.
- [x] Review backend checklist after UIDESIGN-based authentication screen redesign; no backend implementation status changed.
- [x] Review backend checklist after Onboarding Flow UI start; onboarding persistence remains pending.
- [x] Review backend checklist after Income and Expense UI start; income and expense persistence remains pending.
- [x] Update backend checklist after defining RM 1,500 as the single-person low disposable income threshold.
- [ ] Update this checklist whenever backend task status, scope, publishing state, or implementation progress changes.
