package com.mantapp.app.ui.state

enum class ScreenStatus {
    Idle,
    Loading,
    Success,
    Error,
}

data class AuthUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val returningOnboardedUser: Boolean = false,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val destination: AuthDestination? = null,
)

enum class AuthDestination {
    Onboarding,
    Dashboard,
}

data class OnboardingUiState(
    val currentStepIndex: Int = 0,
    val completedStepCount: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)

data class MoneyEntryUiState(
    val monthlyIncome: String = "",
    val totalEssentialExpenses: String = "",
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)

data class RecommendationUiState(
    val disposableIncome: String = "",
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)

data class DashboardUiState(
    val monthlyIncome: String = "",
    val disposableIncome: String = "",
    val pointsBalance: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
)

data class ProgressUiState(
    val pendingProofCount: Int = 0,
    val approvedActionCount: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
)

data class RewardsUiState(
    val pointsBalance: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)

data class SettingsUiState(
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)
