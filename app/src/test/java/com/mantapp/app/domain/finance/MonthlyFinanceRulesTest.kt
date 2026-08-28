package com.mantapp.app.domain.finance

import com.mantapp.app.domain.model.EssentialExpenseCategories
import com.mantapp.app.domain.model.ExpenseEntry
import com.mantapp.app.domain.model.MonthlyFinance
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonthlyFinanceRulesTest {
    @Test
    fun totalEssentialExpenses_addsEveryExpenseCategory() {
        val expenses = EssentialExpenseCategories.all.mapIndexed { index, category ->
            ExpenseEntry(
                categoryKey = category.key,
                amount = BigDecimal((index + 1) * 10),
            )
        }

        assertEquals(BigDecimal("660"), MonthlyFinanceRules.totalEssentialExpenses(expenses))
    }

    @Test
    fun monthlyFinance_calculatesTotalExpensesAndDisposableIncome() {
        val finance = MonthlyFinance(
            userId = "user-1",
            monthlyIncome = BigDecimal("5000.00"),
            expenses = listOf(
                ExpenseEntry(EssentialExpenseCategories.HOUSING, BigDecimal("1200.00")),
                ExpenseEntry(EssentialExpenseCategories.GROCERIES, BigDecimal("450.50")),
                ExpenseEntry(EssentialExpenseCategories.TRANSPORTATION, BigDecimal("200.00")),
            ),
            updatedAt = Instant.EPOCH,
        )

        assertEquals(BigDecimal("1850.50"), finance.totalEssentialExpenses)
        assertEquals(BigDecimal("3149.50"), finance.disposableIncome)
    }

    @Test
    fun isLowDisposableIncome_isTrueAtSinglePersonThreshold() {
        assertTrue(MonthlyFinanceRules.isLowDisposableIncome(BigDecimal("1500.00")))
    }

    @Test
    fun isLowDisposableIncome_isFalseAboveSinglePersonThreshold() {
        assertFalse(MonthlyFinanceRules.isLowDisposableIncome(BigDecimal("1500.01")))
    }

    @Test
    fun isLowDisposableIncome_isTrueForNegativeDisposableIncome() {
        assertTrue(MonthlyFinanceRules.isLowDisposableIncome(BigDecimal("-1.00")))
    }

    @Test
    fun lowDisposableIncomeThreshold_scalesByHouseholdMemberCountWhenAvailable() {
        assertEquals(
            BigDecimal("4500.00"),
            MonthlyFinanceRules.lowDisposableIncomeThreshold(householdMemberCount = 3),
        )
    }

    @Test
    fun validate_rejectsNegativeIncomeNegativeExpenseAndUnknownCategory() {
        val finance = MonthlyFinance(
            userId = "user-1",
            monthlyIncome = BigDecimal("-1.00"),
            expenses = listOf(
                ExpenseEntry(EssentialExpenseCategories.HOUSING, BigDecimal("-100.00")),
                ExpenseEntry("unexpected", BigDecimal("20.00")),
            ),
            updatedAt = Instant.EPOCH,
        )

        val validation = MonthlyFinanceRules.validate(finance)

        assertFalse(validation.isValid)
        assertEquals("Monthly income cannot be negative.", validation.fieldErrors["monthlyIncome"])
        assertEquals(
            "Expense amount cannot be negative.",
            validation.fieldErrors["expense.${EssentialExpenseCategories.HOUSING}"],
        )
        assertEquals(
            "Unknown essential expense category.",
            validation.fieldErrors["expense.unexpected"],
        )
    }

    @Test
    fun validate_acceptsEveryCanonicalExpenseCategory() {
        val finance = MonthlyFinance(
            userId = "user-1",
            monthlyIncome = BigDecimal("5000.00"),
            expenses = EssentialExpenseCategories.all.map { category ->
                ExpenseEntry(category.key, BigDecimal("1.00"))
            },
            updatedAt = Instant.EPOCH,
        )

        assertTrue(MonthlyFinanceRules.validate(finance).isValid)
    }
}
