package com.mantapp.app.domain.model

import com.mantapp.app.domain.finance.MonthlyFinanceRules
import java.math.BigDecimal
import java.time.Instant

data class UserAccount(
    val id: String,
    val displayName: String,
    val email: String,
    val createdAt: Instant,
)

data class SessionState(
    val activeUserId: String? = null,
    val isOnboardingComplete: Boolean = false,
)

data class FinancialProfile(
    val userId: String,
    val employmentStatus: String? = null,
    val incomeStability: String? = null,
    val debtStatus: String? = null,
    val debtType: String? = null,
    val emergencySavingsStatus: String? = null,
    val emergencySavingsCoverageMonths: String? = null,
    val mainFinancialGoals: String? = null,
    val shortTermPurchaseGoal: String? = null,
    val riskTolerance: String? = null,
    val budgetingPreference: String? = null,
    val upcomingMajorExpenses: String? = null,
    val answers: Map<String, String> = emptyMap(),
    val completedAt: Instant? = null,
)

object FinancialProfileFields {
    const val EMPLOYMENT_STATUS = "employment_status"
    const val INCOME_STABILITY = "income_stability"
    const val DEBT_STATUS = "debt_status"
    const val DEBT_TYPES = "debt_types"
    const val EMERGENCY_SAVINGS_STATUS = "emergency_savings_status"
    const val EMERGENCY_SAVINGS_COVERAGE = "emergency_savings_coverage"
    const val MAIN_FINANCIAL_GOALS = "main_financial_goals"
    const val SHORT_TERM_PURCHASE_GOAL = "short_term_purchase_goal"
    const val RISK_TOLERANCE = "risk_tolerance"
    const val BUDGETING_PREFERENCE = "budgeting_preference"
    const val UPCOMING_MAJOR_EXPENSES = "upcoming_major_expenses"
}

data class FinancialProfileValidationResult(
    val missingFieldKeys: List<String>,
) {
    val isValid: Boolean
        get() = missingFieldKeys.isEmpty()
}

data class ExpenseEntry(
    val categoryKey: String,
    val amount: BigDecimal,
)

data class ExpenseCategory(
    val key: String,
    val label: String,
)

object EssentialExpenseCategories {
    const val HOUSING = "housing"
    const val UTILITIES = "utilities"
    const val GROCERIES = "groceries"
    const val TRANSPORTATION = "transportation"
    const val INSURANCE = "insurance"
    const val CREDIT_CARD_MINIMUM = "credit_card_minimum"
    const val LOAN_REPAYMENT = "loan_repayment"
    const val PHONE_INTERNET = "phone_internet"
    const val EDUCATION = "education"
    const val SUBSCRIPTIONS = "subscriptions"
    const val OTHER_COMMITMENTS = "other_commitments"

    val all: List<ExpenseCategory> = listOf(
        ExpenseCategory(HOUSING, "Rent or housing"),
        ExpenseCategory(UTILITIES, "Utilities"),
        ExpenseCategory(GROCERIES, "Groceries"),
        ExpenseCategory(TRANSPORTATION, "Transportation"),
        ExpenseCategory(INSURANCE, "Insurance"),
        ExpenseCategory(CREDIT_CARD_MINIMUM, "Credit card minimum payment"),
        ExpenseCategory(LOAN_REPAYMENT, "Loan repayment"),
        ExpenseCategory(PHONE_INTERNET, "Phone and internet"),
        ExpenseCategory(EDUCATION, "Education"),
        ExpenseCategory(SUBSCRIPTIONS, "Subscriptions"),
        ExpenseCategory(OTHER_COMMITMENTS, "Other necessary commitments"),
    )

    val allowedKeys: Set<String> = all.mapTo(mutableSetOf()) { it.key }
}

data class MonthlyFinanceValidationResult(
    val fieldErrors: Map<String, String>,
) {
    val isValid: Boolean
        get() = fieldErrors.isEmpty()
}

data class MonthlyFinance(
    val userId: String,
    val monthlyIncome: BigDecimal,
    val expenses: List<ExpenseEntry>,
    val updatedAt: Instant,
) {
    val totalEssentialExpenses: BigDecimal
        get() = MonthlyFinanceRules.totalEssentialExpenses(expenses)

    val disposableIncome: BigDecimal
        get() = MonthlyFinanceRules.disposableIncome(monthlyIncome, expenses)
}

data class SavedRecommendation(
    val id: String,
    val userId: String,
    val source: String,
    val rationale: String,
    val createdAt: Instant,
)

enum class ProgressActionTypeDomain {
    WeeklyCheckIn,
    SavingsAction,
    DebtRepayment,
    AllocationComplete,
}

enum class VerificationStatus {
    NotRequired,
    Pending,
    Approved,
    Rejected,
}

data class ProgressLog(
    val id: String,
    val userId: String,
    val recommendationId: String?,
    val actionType: ProgressActionTypeDomain,
    val amount: BigDecimal?,
    val note: String,
    val proofReference: String? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.NotRequired,
    val createdAt: Instant,
)

data class RewardCatalogueItem(
    val id: String,
    val merchant: String,
    val title: String,
    val pointCost: Int,
    val isAvailable: Boolean = true,
)

enum class PointTransactionType {
    Earned,
    Redeemed,
}

data class PointTransaction(
    val id: String,
    val userId: String,
    val points: Int,
    val type: PointTransactionType,
    val reason: String,
    val linkedProgressLogId: String? = null,
    val linkedRewardId: String? = null,
    val createdAt: Instant,
)
