# Mantapp Project Agent Guide

## Purpose

This file is the working specification for future agents contributing to Mantapp. Treat it as the first source to read before planning or implementing changes in this repository.

Mantapp is an AI-powered personal finance application that helps users understand monthly disposable income and allocate it toward savings, debt repayment, long-term goals, flexible spending, and reward-based progress. The product is being developed with an agile methodology, so the PDF product documentation is the current baseline rather than a permanent contract.

## Current Workspace State

At the time this harness was created, the `D:\Mantapp` workspace did not contain application source files. The `.harness` files are therefore project-foundation documents, not a summary of an existing implementation.

Future agents should inspect the repository again before making changes and update these files when the actual codebase diverges from the current assumptions.

The original combined `.harness/TODOLIST.md` has been split into:

- `.harness/FRONTENDTODOLIST.md` for Jetpack Compose UI, navigation, screen state, charts, copy, and frontend validation.
- `.harness/BACKENDTODOLIST.md` for Room, repositories, domain logic, recommendation rules, reward logic, proof verification state, and backend tests.

## Product Baseline

Use the current Mantapp product documentation and user-provided prompt as the baseline for the MVP:

- User registration and login.
- Required onboarding questionnaire before financial data entry.
- User financial profile with employment status, income stability, debt status, debt types, emergency savings, goals, risk preference, budgeting preference, and major upcoming expenses.
- Monthly salary and essential expense input.
- Disposable income calculation.
- Rule-based financial allocation recommendations.
- Optional LLM layer for explanations, contextual insights, and later personalization.
- Dashboard showing salary, expenses, disposable income, recommended allocations, progress, and reward points.
- Progress tracking for savings, debt repayment, check-ins, and monthly plan completion.
- Simulated proof upload and verification flow.
- Point-based reward system and simulated voucher redemption.
- Settings page for profile management, AI configuration, preferences, and disclaimer access.

## MVP Scope

Prioritize an offline/local MVP before external integrations:

- Kotlin application.
- Jetpack Compose UI.
- MVVM architecture.
- Room database using SQLite.
- Local deterministic recommendation engine first.
- LLM integration prepared behind an interface, but not required for initial functionality.
- Simulated proof upload and manual verification states.
- Local reward catalogue and point transactions.
- Charts using Vico or MPAndroidChart.

Out of scope for the first implementation pass:

- Real banking integrations.
- Automated transfers.
- Real merchant voucher redemption.
- Production-grade KYC or financial institution integrations.
- Licensed financial advice or investment execution.

## Android Baseline Decisions

- Create the Android application from scratch in this repository.
- Package name: `com.mantapp.app`.
- App display name: `Mantapp`.
- Minimum SDK: 26.
- Target SDK: 34.
- Android Gradle Plugin: `9.2.1`.
- Kotlin support: AGP `9.2.1` built-in Kotlin support; do not apply the obsolete `org.jetbrains.kotlin.android` plugin.
- Kotlin Compose plugin/version source: `2.3.21`, pinned to the Hilt-compatible Kotlin `2.3.x` metadata line.
- Jetpack Compose BOM: `2024.06.00`.
- Chart library: Vico Compose `1.16.1`.
- AndroidX dependencies must remain compatible with compile SDK 34 unless the user explicitly approves a compile SDK increase.
- KSP plugin: `2.3.9`, kept on the Kotlin `2.3.x`-compatible line.
- Room: `2.6.1`.
- Dependency injection: Hilt `2.59.2` with AndroidX Hilt Navigation Compose `1.2.0`.
- Test stack: JUnit 4, AndroidX Test, Espresso, Compose UI testing, and MockK.
- Authentication is local-only for the MVP. Do not introduce backend authentication providers, external auth SDKs, or network-backed session handling.
- Persist user session state and financial profiles entirely offline using Room.
- Current authentication screens are UI-only simulated local auth until Room-backed user/session persistence is implemented.

## Local Toolchain

- Local JDK 17 is installed at `D:\Mantapp\.tools\jdk17` and should be used as `JAVA_HOME` for Gradle verification.
- Local Android SDK is installed at `D:\Mantapp\.android-sdk`.
- `local.properties` points `sdk.dir` to the local Android SDK and remains untracked.
- `.tools/` and `.android-sdk/` are local-only directories and must stay ignored by Git.
- Verified SDK packages include Android SDK Platform 34, Android SDK Build-Tools 34.0.0, and Android SDK Platform-Tools.
- Gradle verification command used for the foundation pass: `.\gradlew.bat testDebugUnitTest assembleDebug`.

## Architecture Direction

Use MVVM with clear module boundaries:

- `data`: Room entities, DAOs, database setup, repositories, local data sources.
- `domain`: recommendation rules, disposable income calculation, reward logic, validation, core models.
- `di`: Hilt modules and dependency graph bindings.
- `ui`: Jetpack Compose screens, shared components, navigation, state rendering, user interactions.
- `viewmodel`: screen state, use-case coordination, input validation, repository calls.

Recommended modules or feature packages:

- User Profile Module.
- Onboarding Module.
- Income and Expense Module.
- AI Recommendation Module.
- Dashboard Module.
- Progress Tracking Module.
- Reward Module.
- Settings Module.

## Core Data Model

The Room database should expand around these primary entities:

- `User`: account details and basic user profile fields.
- `FinancialProfile`: onboarding answers, monthly salary, employment status, income stability, debt status, debt type, emergency fund status, risk preference, budgeting approach, financial goals, and major expenses.
- `Expense`: monthly essential expenses by category and amount.
- `Recommendation`: generated allocation plan, percentages, amounts, rationale, source, and creation time.
- `ProgressLog`: monthly or weekly plan progress, action type, completion status, proof reference, and verification status.
- `Reward`: simulated reward catalogue item, merchant, point cost, availability, and redemption metadata.
- `PointTransaction`: points earned or redeemed, reason, linked progress log or reward, and timestamp.

Use migrations once the schema exists. Avoid destructive schema changes unless the user explicitly approves them.

## Recommendation Engine Rules

Prioritize deterministic logic before LLM output. The initial rule engine should:

- Calculate disposable income as monthly salary minus total essential expenses.
- Prioritize emergency savings when the user has no or insufficient emergency fund.
- Prioritize debt repayment when high-interest or credit-card debt exists.
- Allocate more toward long-term savings or investment preparation when income is stable and emergency savings are sufficient.
- Reserve funds for short-term goals when the user has selected them.
- Always leave a realistic flexible spending amount where possible.
- Handle low or negative disposable income by recommending expense review and avoiding unrealistic savings/investment allocations.

Example allocation patterns from the current product baseline:

- No emergency fund: 50% emergency savings, 20% debt repayment, 10% long-term savings, 20% flexible spending.
- High credit-card debt: 20% emergency savings, 50% debt repayment, 10% long-term savings, 20% flexible spending.
- Stable finances: 25% emergency or goal savings, 25% retirement or investment preparation, 20% debt repayment, 30% flexible spending.
- Low disposable income: 40% emergency savings, 20% debt repayment, 0% long-term investment, 40% flexible spending.

Prepare an LLM-facing interface later for explanations and insights, but keep financial percentages explainable and testable through local rules.

## Reward and Proof Verification Rules

Implement proof upload as simulated local verification for now:

- Users can attach or reference proof for savings, debt repayment, or allocation completion.
- Verification statuses should include `Pending`, `Approved`, and `Rejected`.
- Points should be awarded only after approval.
- Point transactions should be immutable ledger-style records.
- Reward redemptions should subtract points and record the redemption.

Example point values:

- Complete monthly financial profile: 50 points.
- Follow savings recommendation: 100 points.
- Meet debt repayment target: 100 points.
- Complete weekly progress check-in: 25 points.
- Maintain a 3-month savings streak: 300 points.
- Reduce unnecessary spending: 75 points.

Example simulated rewards:

- FamilyMart RM5 Voucher: 500 points.
- TGV Movie Ticket RM5 Voucher: 500 points.
- GSC Snack Combo Voucher: 800 points.
- Petronas RM5 Voucher: 1200 points.

## UI and UX Direction

Build the app as a real finance tool, not a marketing page.

- Use clear, non-technical language.
- Keep financial summaries prominent and easy to scan.
- Make onboarding step-based and calm.
- Use form validation for money inputs and required profile answers.
- Make the dashboard action-oriented: show what the user has, what remains, what is recommended, and what to do next.
- Remake the Profile page into a Settings page with profile data, AI configuration, app preferences, and financial disclaimer.
- Include the educational disclaimer in settings and recommendation output.

Required main screens:

- Login and registration.
- Onboarding questionnaire.
- Financial profile setup.
- Income and expense input.
- Recommendation screen.
- Dashboard.
- Progress tracking.
- Rewards and redemption.
- Settings and disclaimer.

## Financial Safety

Mantapp must avoid representing recommendations as licensed financial, investment, tax, or legal advice.

Use this disclaimer where appropriate:

> Mantapp provides general budgeting guidance based on the information entered by the user. Recommendations are for educational purposes only and should not be considered professional financial, investment, or legal advice. Users should consult a qualified financial advisor before making major financial decisions.

## Development Practices

- Read `.harness/AGENTS.md`, `.harness/PLAN.md`, `.harness/FRONTENDTODOLIST.md`, and `.harness/BACKENDTODOLIST.md` before implementation work.
- Use `.harness/FRONTENDTODOLIST.md` for UI-facing work and `.harness/BACKENDTODOLIST.md` for data, persistence, and domain logic work.
- After each completed sequence of tasks, update both `.harness/FRONTENDTODOLIST.md` and `.harness/BACKENDTODOLIST.md` in the same turn when task status, scope, publishing state, or implementation progress changes.
- Keep changes scoped to the current task.
- Prefer existing project patterns once source code exists.
- Update `.harness` files when product decisions change.
- Add tests for deterministic rules, disposable income calculation, reward points, and database behavior.
- Do not introduce network calls, banking APIs, real AI calls, or real merchant redemption without explicit approval.

## Git Attribution

- Do not use tool, assistant, or automation identities as Git commit authors.
- Codex must never appear as a Git author, committer, co-author, or GitHub contributor for this repository.
- Attribute repository commits to the actual human contributor or maintainer responsible for the change unless the user explicitly requests a different author.
- If a generated commit accidentally uses an assistant identity, tool identity, or any non-human identity, rewrite it before pushing so GitHub contributors reflect the correct human account.

## Repository and Publishing Notes

The user intends to review the harness files first. After approval, future work may be pushed to:

`https://github.com/pomudev/mantapp`

Do not push, commit, or open a pull request until the user gives the green light.
