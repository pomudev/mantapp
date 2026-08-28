package com.mantapp.app.domain.finance

import com.mantapp.app.domain.model.EssentialExpenseCategories
import com.mantapp.app.domain.model.ExpenseEntry
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.model.MonthlyFinanceValidationResult
import java.math.BigDecimal

object MonthlyFinanceRules {
    val LOW_DISPOSABLE_INCOME_THRESHOLD_SINGLE_PERSON: BigDecimal = BigDecimal("1500.00")

    fun totalEssentialExpenses(expenses: List<ExpenseEntry>): BigDecimal {
        return expenses.fold(BigDecimal.ZERO) { total, entry -> total + entry.amount }
    }

    fun disposableIncome(monthlyIncome: BigDecimal, expenses: List<ExpenseEntry>): BigDecimal {
        return monthlyIncome - totalEssentialExpenses(expenses)
    }

    fun isLowDisposableIncome(disposableIncome: BigDecimal, householdMemberCount: Int = 1): Boolean {
        return disposableIncome <= lowDisposableIncomeThreshold(householdMemberCount)
    }

    fun lowDisposableIncomeThreshold(householdMemberCount: Int = 1): BigDecimal {
        val memberCount = householdMemberCount.coerceAtLeast(1)
        return LOW_DISPOSABLE_INCOME_THRESHOLD_SINGLE_PERSON.multiply(BigDecimal(memberCount))
    }

    fun validate(finance: MonthlyFinance): MonthlyFinanceValidationResult {
        val errors = buildMap {
            if (finance.userId.isBlank()) {
                put("userId", "A signed-in user is required.")
            }

            if (finance.monthlyIncome < BigDecimal.ZERO) {
                put("monthlyIncome", "Monthly income cannot be negative.")
            }

            if (finance.expenses.isEmpty()) {
                put("expenses", "Add at least one essential expense.")
            }

            finance.expenses.forEach { expense ->
                when {
                    expense.categoryKey !in EssentialExpenseCategories.allowedKeys -> {
                        put("expense.${expense.categoryKey}", "Unknown essential expense category.")
                    }

                    expense.amount < BigDecimal.ZERO -> {
                        put("expense.${expense.categoryKey}", "Expense amount cannot be negative.")
                    }
                }
            }
        }
        return MonthlyFinanceValidationResult(errors)
    }
}
