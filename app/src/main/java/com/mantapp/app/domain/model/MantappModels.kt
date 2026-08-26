package com.mantapp.app.domain.model

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
    val answers: Map<String, String>,
    val completedAt: Instant? = null,
)

data class ExpenseEntry(
    val categoryKey: String,
    val amount: BigDecimal,
)

data class MonthlyFinance(
    val userId: String,
    val monthlyIncome: BigDecimal,
    val expenses: List<ExpenseEntry>,
    val updatedAt: Instant,
) {
    val totalEssentialExpenses: BigDecimal
        get() = expenses.fold(BigDecimal.ZERO) { total, entry -> total + entry.amount }

    val disposableIncome: BigDecimal
        get() = monthlyIncome - totalEssentialExpenses
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
