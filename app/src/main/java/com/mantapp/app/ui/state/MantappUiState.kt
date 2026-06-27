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
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
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
    val answers: Map<String, String> = emptyMap(),
    val skippedQuestionKeys: Set<String> = emptySet(),
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
    val isComplete: Boolean = false,
)

data class MoneyEntryUiState(
    val monthlyIncome: String = "",
    val monthlyIncomeError: String? = null,
    val expenseInputs: Map<String, String> = emptyMap(),
    val expenseErrors: Map<String, String> = emptyMap(),
    val totalEssentialExpenses: String = "",
    val disposableIncome: String = "",
    val isLowOrNegativeDisposableIncome: Boolean = false,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

data class RecommendationUiState(
    val monthlyIncome: String = "",
    val totalEssentialExpenses: String = "",
    val disposableIncome: String = "",
    val decisionSource: RecommendationDecisionSourceUi = RecommendationDecisionSourceUi.LocalFallback,
    val allocationItems: List<RecommendationAllocationUiModel> = emptyList(),
    val guidanceItems: List<RecommendationGuidanceUiModel> = emptyList(),
    val safetyNotes: List<String> = emptyList(),
    val rationale: String = "",
    val missingInputs: List<String> = emptyList(),
    val aiExplanation: String? = null,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
)

enum class RecommendationDecisionSourceUi {
    AiFinal,
    LocalFallback,
}

data class RecommendationAllocationUiModel(
    val label: String,
    val percentage: Int,
    val amount: String,
    val note: String,
)

data class RecommendationGuidanceUiModel(
    val title: String,
    val message: String,
    val priorityAdjustmentPercent: Int? = null,
)

data class DashboardUiState(
    val monthlyIncome: String = "",
    val totalEssentialExpenses: String = "",
    val disposableIncome: String = "",
    val allocationItems: List<RecommendationAllocationUiModel> = emptyList(),
    val missingInputs: List<String> = emptyList(),
    val isLowOrNegativeDisposableIncome: Boolean = false,
    val progressTitle: String = "No actions logged yet",
    val progressMessage: String = "Start tracking once you choose a recommendation action.",
    val nextRecommendedAction: String = "",
    val pointsBalance: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
)

data class ProgressUiState(
    val selectedActionType: ProgressActionType = ProgressActionType.WeeklyCheckIn,
    val amount: String = "",
    val amountError: String? = null,
    val note: String = "",
    val noteError: String? = null,
    val activeRecommendationLabel: String = "Current fallback allocation",
    val logs: List<ProgressLogUiModel> = emptyList(),
    val monthlyCompletionCount: Int = 0,
    val monthlyTargetCount: Int = 4,
    val streakCandidateWeeks: Int = 0,
    val status: ScreenStatus = ScreenStatus.Idle,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

enum class ProgressActionType {
    WeeklyCheckIn,
    SavingsAction,
    DebtRepayment,
    AllocationComplete,
}

data class ProgressLogUiModel(
    val id: String,
    val actionType: ProgressActionType,
    val title: String,
    val amount: String,
    val note: String,
    val linkedRecommendationLabel: String,
    val completionStatus: String,
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
