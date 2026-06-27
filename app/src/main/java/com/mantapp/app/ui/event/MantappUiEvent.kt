package com.mantapp.app.ui.event

import com.mantapp.app.ui.state.ProgressActionType

sealed interface AuthEvent {
    data class DisplayNameChanged(val value: String) : AuthEvent
    data class EmailChanged(val value: String) : AuthEvent
    data class PasswordChanged(val value: String) : AuthEvent
    data class ConfirmPasswordChanged(val value: String) : AuthEvent
    data class ReturningOnboardedUserChanged(val value: Boolean) : AuthEvent
    data object SubmitLogin : AuthEvent
    data object SubmitRegistration : AuthEvent
    data object NavigationHandled : AuthEvent
}

sealed interface OnboardingEvent {
    data class AnswerChanged(val questionKey: String, val value: String) : OnboardingEvent
    data object Continue : OnboardingEvent
    data object Back : OnboardingEvent
    data object SubmitProfile : OnboardingEvent
}

sealed interface MoneyEntryEvent {
    data class MonthlyIncomeChanged(val value: String) : MoneyEntryEvent
    data class ExpenseChanged(val categoryKey: String, val value: String) : MoneyEntryEvent
    data object SubmitFinancialInputs : MoneyEntryEvent
}

sealed interface ProgressEvent {
    data class ActionTypeChanged(val value: ProgressActionType) : ProgressEvent
    data class AmountChanged(val value: String) : ProgressEvent
    data class NoteChanged(val value: String) : ProgressEvent
    data object SubmitProgressLog : ProgressEvent
}

sealed interface ProofEvent {
    data class SubmitProof(val progressLogId: String, val proofReference: String) : ProofEvent
    data class ApproveProof(val progressLogId: String) : ProofEvent
    data class RejectProof(val progressLogId: String, val reason: String) : ProofEvent
}

sealed interface RewardEvent {
    data class RedeemReward(val rewardId: String) : RewardEvent
}

sealed interface SettingsEvent {
    data class UpdateProfileField(val fieldKey: String, val value: String) : SettingsEvent
    data object SaveSettings : SettingsEvent
    data object ResetLocalDataRequested : SettingsEvent
}
