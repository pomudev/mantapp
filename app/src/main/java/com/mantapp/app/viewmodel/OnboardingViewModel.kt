package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mantapp.app.ui.event.OnboardingEvent
import com.mantapp.app.ui.state.OnboardingUiState
import com.mantapp.app.ui.state.ScreenStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.AnswerChanged -> updateAnswer(event.questionKey, event.value)
            OnboardingEvent.Continue -> continueToNextStep()
            OnboardingEvent.Back -> goBack()
            OnboardingEvent.SubmitProfile -> submitProfile()
        }
    }

    private fun updateAnswer(questionKey: String, value: String) {
        _state.update { current ->
            val updatedAnswers = current.answers + (questionKey to value)
            current.copy(
                answers = updatedAnswers,
                skippedQuestionKeys = skippedQuestions(updatedAnswers),
                status = ScreenStatus.Idle,
                errorMessage = null,
            )
        }
    }

    private fun continueToNextStep() {
        _state.update { current ->
            val activeKeys = activeQuestionKeys(current.answers)
            val currentKey = activeKeys[current.currentStepIndex]
            val answer = current.answers[currentKey].orEmpty()
            if (answer.isBlank()) {
                current.copy(
                    status = ScreenStatus.Error,
                    errorMessage = "Choose an answer before moving on.",
                )
            } else {
                val nextIndex = (current.currentStepIndex + 1).coerceAtMost(activeKeys.lastIndex)
                current.copy(
                    currentStepIndex = nextIndex,
                    completedStepCount = nextIndex,
                    skippedQuestionKeys = skippedQuestions(current.answers),
                    status = ScreenStatus.Idle,
                    errorMessage = null,
                )
            }
        }
    }

    private fun goBack() {
        _state.update { current ->
            current.copy(
                currentStepIndex = (current.currentStepIndex - 1).coerceAtLeast(0),
                status = ScreenStatus.Idle,
                errorMessage = null,
            )
        }
    }

    private fun submitProfile() {
        _state.update { current ->
            val missingKey = activeQuestionKeys(current.answers)
                .filterNot { it == REVIEW_STEP_KEY }
                .firstOrNull { current.answers[it].isNullOrBlank() }
            if (missingKey != null) {
                current.copy(
                    currentStepIndex = activeQuestionKeys(current.answers).indexOf(missingKey),
                    status = ScreenStatus.Error,
                    errorMessage = "Complete this answer before finishing setup.",
                )
            } else {
                current.copy(
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    isComplete = true,
                )
            }
        }
    }

    private fun activeQuestionKeys(answers: Map<String, String>): List<String> {
        return if (answers[DEBT_STATUS_KEY] == NO_DEBT_ANSWER) {
            QUESTION_KEYS.filterNot { it == DEBT_TYPES_KEY }
        } else {
            QUESTION_KEYS
        }
    }

    private fun skippedQuestions(answers: Map<String, String>): Set<String> {
        return if (answers[DEBT_STATUS_KEY] == NO_DEBT_ANSWER) {
            setOf(DEBT_TYPES_KEY)
        } else {
            emptySet()
        }
    }

    private companion object {
        const val DEBT_STATUS_KEY = "debt_status"
        const val DEBT_TYPES_KEY = "debt_types"
        const val NO_DEBT_ANSWER = "No debt right now"
        const val REVIEW_STEP_KEY = "review"

        val QUESTION_KEYS = listOf(
            "employment_status",
            "income_stability",
            DEBT_STATUS_KEY,
            DEBT_TYPES_KEY,
            "emergency_savings_status",
            "emergency_savings_coverage",
            "main_financial_goals",
            "short_term_purchase_goal",
            "risk_tolerance",
            "budgeting_preference",
            "upcoming_major_expenses",
            REVIEW_STEP_KEY,
        )
    }
}
