package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantapp.app.domain.model.ExpenseEntry
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.repository.AuthRepository
import com.mantapp.app.domain.repository.MoneyRepository
import com.mantapp.app.ui.event.MoneyEntryEvent
import com.mantapp.app.ui.state.MoneyEntryUiState
import com.mantapp.app.ui.state.ScreenStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MoneyEntryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val moneyRepository: MoneyRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MoneyEntryUiState())
    val state: StateFlow<MoneyEntryUiState> = _state.asStateFlow()

    fun onEvent(event: MoneyEntryEvent) {
        when (event) {
            is MoneyEntryEvent.MonthlyIncomeChanged -> updateMonthlyIncome(event.value)
            is MoneyEntryEvent.ExpenseChanged -> updateExpense(event.categoryKey, event.value)
            MoneyEntryEvent.SubmitFinancialInputs -> submitFinancialInputs()
        }
    }

    private fun updateMonthlyIncome(value: String) {
        _state.update { current ->
            recalculate(
                current.copy(
                    monthlyIncome = value,
                    monthlyIncomeError = null,
                    status = ScreenStatus.Idle,
                    errorMessage = null,
                    successMessage = null,
                ),
            )
        }
    }

    private fun updateExpense(categoryKey: String, value: String) {
        _state.update { current ->
            recalculate(
                current.copy(
                    expenseInputs = current.expenseInputs + (categoryKey to value),
                    expenseErrors = current.expenseErrors - categoryKey,
                    status = ScreenStatus.Idle,
                    errorMessage = null,
                    successMessage = null,
                ),
            )
        }
    }

    private fun submitFinancialInputs() {
        _state.update { current ->
            val monthlyIncome = current.monthlyIncome.toMoneyOrNull()
            val monthlyIncomeError = when {
                current.monthlyIncome.isBlank() -> "Enter your monthly salary."
                monthlyIncome == null -> "Use a valid amount."
                monthlyIncome < BigDecimal.ZERO -> "Amount cannot be negative."
                else -> null
            }

            val expenseErrors = current.expenseInputs.mapNotNull { (key, value) ->
                when {
                    value.isBlank() -> null
                    value.toMoneyOrNull() == null -> key to "Use a valid amount."
                    else -> null
                }
            }.toMap()

            val missingExpenseMessage = if (current.expenseInputs.values.none { it.isNotBlank() }) {
                "Add at least one essential expense before continuing."
            } else {
                null
            }

            val isValid = monthlyIncomeError == null &&
                expenseErrors.isEmpty() &&
                missingExpenseMessage == null

            val updatedState = recalculate(
                current.copy(
                    monthlyIncomeError = monthlyIncomeError,
                    expenseErrors = expenseErrors,
                    status = if (isValid) ScreenStatus.Loading else ScreenStatus.Error,
                    errorMessage = monthlyIncomeError ?: missingExpenseMessage,
                    successMessage = null,
                ),
            )

            if (isValid && monthlyIncome != null) {
                saveFinancialInputs(updatedState, monthlyIncome)
            }

            updatedState
        }
    }

    private fun saveFinancialInputs(state: MoneyEntryUiState, monthlyIncome: BigDecimal) {
        viewModelScope.launch {
            runCatching {
                val activeUserId = authRepository.session.first().activeUserId
                    ?: error("Sign in before saving cash flow.")
                val expenses = state.expenseInputs.mapNotNull { (key, value) ->
                    val amount = value.toMoneyOrNull()
                    if (amount == null) {
                        null
                    } else {
                        ExpenseEntry(categoryKey = key, amount = amount)
                    }
                }
                moneyRepository.saveMonthlyFinance(
                    MonthlyFinance(
                        userId = activeUserId,
                        monthlyIncome = monthlyIncome,
                        expenses = expenses,
                        updatedAt = Instant.now(),
                    ),
                )
            }.fold(
                onSuccess = {
                    _state.update { current ->
                        current.copy(
                            status = ScreenStatus.Success,
                            errorMessage = null,
                            successMessage = "Your monthly cash flow is ready for recommendations.",
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update { current ->
                        current.copy(
                            status = ScreenStatus.Error,
                            errorMessage = throwable.message ?: "Could not save your cash flow yet.",
                            successMessage = null,
                        )
                    }
                },
            )
        }
    }

    private fun recalculate(state: MoneyEntryUiState): MoneyEntryUiState {
        val monthlyIncome = state.monthlyIncome.toMoneyOrNull() ?: BigDecimal.ZERO
        val totalExpenses = state.expenseInputs.values
            .mapNotNull { it.toMoneyOrNull() }
            .fold(BigDecimal.ZERO) { total, amount -> total + amount }
        val disposableIncome = monthlyIncome - totalExpenses

        return state.copy(
            totalEssentialExpenses = totalExpenses.toDisplayAmount(),
            disposableIncome = disposableIncome.toDisplayAmount(),
            isLowOrNegativeDisposableIncome = disposableIncome <= LOW_DISPOSABLE_INCOME_THRESHOLD,
        )
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

    private companion object {
        val LOW_DISPOSABLE_INCOME_THRESHOLD: BigDecimal = BigDecimal("1500.00")
    }

}
