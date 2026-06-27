package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mantapp.app.ui.event.ProgressEvent
import com.mantapp.app.ui.state.ProgressActionType
import com.mantapp.app.ui.state.ProgressLogUiModel
import com.mantapp.app.ui.state.ProgressUiState
import com.mantapp.app.ui.state.ScreenStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ProgressViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    fun onEvent(event: ProgressEvent) {
        when (event) {
            is ProgressEvent.ActionTypeChanged -> updateActionType(event.value)
            is ProgressEvent.AmountChanged -> updateAmount(event.value)
            is ProgressEvent.NoteChanged -> updateNote(event.value)
            ProgressEvent.SubmitProgressLog -> submitProgressLog()
        }
    }

    private fun updateActionType(value: ProgressActionType) {
        _state.update { current ->
            current.copy(
                selectedActionType = value,
                amountError = null,
                noteError = null,
                status = ScreenStatus.Idle,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    private fun updateAmount(value: String) {
        _state.update { current ->
            current.copy(
                amount = value.filter { it.isDigit() || it == '.' },
                amountError = null,
                status = ScreenStatus.Idle,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    private fun updateNote(value: String) {
        _state.update { current ->
            current.copy(
                note = value,
                noteError = null,
                status = ScreenStatus.Idle,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    private fun submitProgressLog() {
        _state.update { current ->
            val requiresAmount = current.selectedActionType != ProgressActionType.WeeklyCheckIn
            val amount = current.amount.toMoneyOrNull()
            val amountError = when {
                requiresAmount && current.amount.isBlank() -> "Enter the amount you completed."
                current.amount.isNotBlank() && amount == null -> "Use a valid amount."
                current.amount.isNotBlank() && amount != null && amount < BigDecimal.ZERO -> "Amount cannot be negative."
                else -> null
            }
            val noteError = if (current.note.isBlank()) {
                "Add a short note for this progress log."
            } else {
                null
            }

            if (amountError != null || noteError != null) {
                current.copy(
                    amountError = amountError,
                    noteError = noteError,
                    status = ScreenStatus.Error,
                    errorMessage = "Check the highlighted progress details.",
                    successMessage = null,
                )
            } else {
                val log = ProgressLogUiModel(
                    id = "progress-${current.logs.size + 1}",
                    actionType = current.selectedActionType,
                    title = current.selectedActionType.displayTitle(),
                    amount = amount?.toDisplayAmount().orEmpty(),
                    note = current.note.trim(),
                    linkedRecommendationLabel = current.activeRecommendationLabel,
                    completionStatus = "Logged",
                )
                val updatedLogs = listOf(log) + current.logs
                current.copy(
                    amount = "",
                    amountError = null,
                    note = "",
                    noteError = null,
                    logs = updatedLogs,
                    monthlyCompletionCount = updatedLogs.size.coerceAtMost(current.monthlyTargetCount),
                    streakCandidateWeeks = updatedLogs.count { it.actionType == ProgressActionType.WeeklyCheckIn },
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    successMessage = "${log.title} added to this month's progress.",
                )
            }
        }
    }

    private fun String.toMoneyOrNull(): BigDecimal? {
        if (isBlank()) return null
        return runCatching {
            BigDecimal(this).setScale(2, RoundingMode.HALF_UP)
        }.getOrNull()
    }

    private fun BigDecimal.toDisplayAmount(): String {
        return setScale(2, RoundingMode.HALF_UP).toPlainString()
    }
}

fun ProgressActionType.displayTitle(): String {
    return when (this) {
        ProgressActionType.WeeklyCheckIn -> "Weekly check-in"
        ProgressActionType.SavingsAction -> "Savings action"
        ProgressActionType.DebtRepayment -> "Debt repayment"
        ProgressActionType.AllocationComplete -> "Allocation completed"
    }
}
