package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantapp.app.domain.model.FinancialProfileFields
import com.mantapp.app.domain.repository.AuthRepository
import com.mantapp.app.domain.repository.FinancialProfileRepository
import com.mantapp.app.ui.event.OnboardingEvent
import com.mantapp.app.ui.state.OnboardingUiState
import com.mantapp.app.ui.state.ScreenStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val financialProfileRepository: FinancialProfileRepository,
) : ViewModel() {
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
            val validation = financialProfileRepository.validateOnboardingAnswers(current.answers)
            val missingKey = validation.missingFieldKeys.firstOrNull()
            if (!validation.isValid && missingKey != null) {
                current.copy(
                    currentStepIndex = activeQuestionKeys(current.answers).indexOf(missingKey),
                    status = ScreenStatus.Error,
                    errorMessage = "Complete this answer before finishing setup.",
                )
            } else {
                viewModelScope.launch {
                    runCatching {
                        val activeUserId = authRepository.session.first().activeUserId
                            ?: error("Sign in before completing setup.")
                        val profile = financialProfileRepository.createProfileFromOnboardingAnswers(
                            userId = activeUserId,
                            answers = _state.value.answers,
                            completedAtEpochMillis = Instant.now().toEpochMilli(),
                        )
                        financialProfileRepository.saveProfile(profile)
                        authRepository.updateOnboardingComplete(isComplete = true)
                    }.fold(
                        onSuccess = {
                            _state.update { latest ->
                                latest.copy(
                                    status = ScreenStatus.Success,
                                    errorMessage = null,
                                    isComplete = true,
                                )
                            }
                        },
                        onFailure = { throwable ->
                            _state.update { latest ->
                                latest.copy(
                                    status = ScreenStatus.Error,
                                    errorMessage = throwable.message ?: "Could not save your setup yet.",
                                )
                            }
                        },
                    )
                    }
                current.copy(status = ScreenStatus.Loading, errorMessage = null)
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
        const val DEBT_STATUS_KEY = FinancialProfileFields.DEBT_STATUS
        const val DEBT_TYPES_KEY = FinancialProfileFields.DEBT_TYPES
        const val NO_DEBT_ANSWER = "No debt right now"
        const val REVIEW_STEP_KEY = "review"

        val QUESTION_KEYS = listOf(
            FinancialProfileFields.EMPLOYMENT_STATUS,
            FinancialProfileFields.INCOME_STABILITY,
            DEBT_STATUS_KEY,
            DEBT_TYPES_KEY,
            FinancialProfileFields.EMERGENCY_SAVINGS_STATUS,
            FinancialProfileFields.EMERGENCY_SAVINGS_COVERAGE,
            FinancialProfileFields.MAIN_FINANCIAL_GOALS,
            FinancialProfileFields.SHORT_TERM_PURCHASE_GOAL,
            FinancialProfileFields.RISK_TOLERANCE,
            FinancialProfileFields.BUDGETING_PREFERENCE,
            FinancialProfileFields.UPCOMING_MAJOR_EXPENSES,
            REVIEW_STEP_KEY,
        )
    }
}
