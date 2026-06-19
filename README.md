# Mantapp

Mantapp is an AI-powered personal finance application focused on helping users understand monthly disposable income and turn that into practical budgeting actions. The MVP centers on guided onboarding, local financial planning rules, progress tracking, and a reward-based motivation system.

## Product Summary

Mantapp helps users:

- register and log in
- complete an onboarding questionnaire before entering financial data
- build a financial profile covering income stability, debt status, goals, risk preference, budgeting preference, emergency savings, and major upcoming expenses
- enter monthly salary and essential expenses
- calculate disposable income
- receive rule-based allocation recommendations
- track savings, debt repayment, and check-in progress
- upload simulated proof for verification
- earn points and redeem simulated rewards
- manage profile, preferences, AI configuration, and disclaimers from settings

## MVP Scope

The first implementation pass should stay local and offline-first:

- Kotlin application
- Jetpack Compose UI
- MVVM architecture
- Room database backed by SQLite
- deterministic recommendation engine
- optional LLM integration behind an interface for later explanations and personalization
- simulated proof upload and manual verification states
- local reward catalog and point ledger
- chart support using Vico or MPAndroidChart

Out of scope for the initial MVP:

- real banking integrations
- automated transfers
- real voucher redemption
- production KYC
- licensed financial advice or investment execution

## Core Experience

The current product baseline includes these main flows:

- user registration and login
- onboarding questionnaire
- financial profile setup
- income and expense input
- recommendation generation
- dashboard with salary, expenses, disposable income, allocations, progress, and points
- progress tracking and monthly plan completion
- rewards and redemption
- settings and disclaimers

## Architecture Direction

Mantapp should follow MVVM with clear boundaries:

- `data`: Room entities, DAOs, database setup, repositories, and local data sources
- `domain`: disposable income calculation, recommendation rules, reward logic, validation, and core models
- `ui`: Jetpack Compose screens, navigation, rendering, and interactions
- `viewmodel`: screen state, use-case coordination, validation, and repository orchestration

Suggested feature packages:

- User Profile
- Onboarding
- Income and Expense
- AI Recommendation
- Dashboard
- Progress Tracking
- Reward
- Settings

## Data Model

The Room schema is expected to expand around these primary entities:

- `User`
- `FinancialProfile`
- `Expense`
- `Recommendation`
- `ProgressLog`
- `Reward`
- `PointTransaction`

These entities should cover account data, onboarding answers, expenses, generated plans, progress verification, rewards, and point transactions. Once the schema exists, use migrations instead of destructive database changes unless explicitly approved.

## Recommendation Logic

The initial recommendation engine should remain deterministic and explainable.

Key rules:

- calculate disposable income as salary minus total essential expenses
- prioritize emergency savings when the user lacks sufficient reserves
- prioritize debt repayment when high-interest or credit-card debt exists
- allocate more toward long-term savings or investment preparation when finances are stable
- reserve funds for short-term goals when selected
- preserve realistic flexible spending where possible
- handle low or negative disposable income conservatively by recommending expense review

Example allocation patterns:

- no emergency fund: 50% emergency savings, 20% debt repayment, 10% long-term savings, 20% flexible spending
- high credit-card debt: 20% emergency savings, 50% debt repayment, 10% long-term savings, 20% flexible spending
- stable finances: 25% emergency or goal savings, 25% retirement or investment preparation, 20% debt repayment, 30% flexible spending
- low disposable income: 40% emergency savings, 20% debt repayment, 0% long-term investment, 40% flexible spending

## Rewards and Proof Verification

Proof upload is simulated in the MVP:

- users can attach or reference proof for savings, debt repayment, or plan completion
- verification statuses should include `Pending`, `Approved`, and `Rejected`
- points are awarded only after approval
- point transactions should behave like an immutable ledger
- reward redemption should subtract points and record the transaction

Example point values:

- monthly financial profile completion: 50 points
- following a savings recommendation: 100 points
- meeting a debt repayment target: 100 points
- weekly progress check-in: 25 points
- 3-month savings streak: 300 points
- reducing unnecessary spending: 75 points

Example rewards:

- FamilyMart RM5 Voucher: 500 points
- TGV Movie Ticket RM5 Voucher: 500 points
- GSC Snack Combo Voucher: 800 points
- Petronas RM5 Voucher: 1200 points

## UI Direction

Mantapp should behave like a practical finance tool:

- use clear, non-technical language
- keep financial summaries easy to scan
- make onboarding calm and step-based
- validate money inputs and required profile fields
- keep the dashboard action-oriented
- present the Profile area as a Settings page with profile management, AI settings, app preferences, and disclaimer access

## Financial Disclaimer

Mantapp provides general budgeting guidance based on the information entered by the user. Recommendations are for educational purposes only and should not be considered professional financial, investment, or legal advice. Users should consult a qualified financial advisor before making major financial decisions.

## Development Notes

- treat the current product documents and `.harness` files as the MVP baseline
- keep changes scoped to the active task
- prefer deterministic, testable business logic
- add tests for allocation rules, disposable income calculation, reward points, and database behavior
- avoid network calls, real AI calls, banking APIs, and real merchant redemption unless explicitly approved
