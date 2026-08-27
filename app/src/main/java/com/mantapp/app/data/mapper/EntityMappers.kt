package com.mantapp.app.data.mapper

import com.mantapp.app.data.local.entity.ExpenseEntity
import com.mantapp.app.data.local.entity.FinancialProfileEntity
import com.mantapp.app.data.local.entity.MonthlyFinanceEntity
import com.mantapp.app.data.local.entity.PointTransactionEntity
import com.mantapp.app.data.local.entity.ProgressLogEntity
import com.mantapp.app.data.local.entity.RecommendationEntity
import com.mantapp.app.data.local.entity.RewardEntity
import com.mantapp.app.data.local.entity.UserEntity
import com.mantapp.app.domain.model.ExpenseEntry
import com.mantapp.app.domain.model.FinancialProfileFields
import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.PointTransactionType
import com.mantapp.app.domain.model.ProgressActionTypeDomain
import com.mantapp.app.domain.model.ProgressLog
import com.mantapp.app.domain.model.RewardCatalogueItem
import com.mantapp.app.domain.model.SavedRecommendation
import com.mantapp.app.domain.model.UserAccount
import com.mantapp.app.domain.model.VerificationStatus
import java.math.BigDecimal
import java.time.Instant
import org.json.JSONObject

fun UserEntity.toDomain(): UserAccount {
    return UserAccount(
        id = id,
        displayName = displayName,
        email = email,
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    )
}

fun FinancialProfileEntity.toDomain(): FinancialProfile {
    val answerMap = answersJson.toAnswerMap()
    return FinancialProfile(
        userId = userId,
        employmentStatus = employmentStatus ?: answerMap[FinancialProfileFields.EMPLOYMENT_STATUS],
        incomeStability = incomeStability ?: answerMap[FinancialProfileFields.INCOME_STABILITY],
        debtStatus = debtStatus ?: answerMap[FinancialProfileFields.DEBT_STATUS],
        debtType = debtType ?: answerMap[FinancialProfileFields.DEBT_TYPES],
        emergencySavingsStatus = emergencySavingsStatus ?: answerMap[FinancialProfileFields.EMERGENCY_SAVINGS_STATUS],
        emergencySavingsCoverageMonths = emergencySavingsCoverageMonths
            ?: answerMap[FinancialProfileFields.EMERGENCY_SAVINGS_COVERAGE],
        mainFinancialGoals = mainFinancialGoals ?: answerMap[FinancialProfileFields.MAIN_FINANCIAL_GOALS],
        shortTermPurchaseGoal = shortTermPurchaseGoal ?: answerMap[FinancialProfileFields.SHORT_TERM_PURCHASE_GOAL],
        riskTolerance = riskTolerance ?: answerMap[FinancialProfileFields.RISK_TOLERANCE],
        budgetingPreference = budgetingPreference ?: answerMap[FinancialProfileFields.BUDGETING_PREFERENCE],
        upcomingMajorExpenses = upcomingMajorExpenses ?: answerMap[FinancialProfileFields.UPCOMING_MAJOR_EXPENSES],
        answers = answerMap,
        completedAt = completedAtEpochMillis?.let(Instant::ofEpochMilli),
    )
}

fun FinancialProfile.toEntity(): FinancialProfileEntity {
    val normalizedAnswers = toAnswerMap()
    return FinancialProfileEntity(
        userId = userId,
        answersJson = normalizedAnswers.toJsonString(),
        employmentStatus = employmentStatus.normalized(),
        incomeStability = incomeStability.normalized(),
        debtStatus = debtStatus.normalized(),
        debtType = debtType.normalized(),
        emergencySavingsStatus = emergencySavingsStatus.normalized(),
        emergencySavingsCoverageMonths = emergencySavingsCoverageMonths.normalized(),
        mainFinancialGoals = mainFinancialGoals.normalized(),
        shortTermPurchaseGoal = shortTermPurchaseGoal.normalized(),
        riskTolerance = riskTolerance.normalized(),
        budgetingPreference = budgetingPreference.normalized(),
        upcomingMajorExpenses = upcomingMajorExpenses.normalized(),
        completedAtEpochMillis = completedAt?.toEpochMilli(),
    )
}

fun MonthlyFinance.toExpenseEntities(): List<ExpenseEntity> {
    return expenses.map { expense ->
        ExpenseEntity(
            userId = userId,
            categoryKey = expense.categoryKey,
            amount = expense.amount.toPlainString(),
            updatedAtEpochMillis = updatedAt.toEpochMilli(),
        )
    }
}

fun MonthlyFinance.toEntity(): MonthlyFinanceEntity {
    return MonthlyFinanceEntity(
        userId = userId,
        monthlyIncome = monthlyIncome.toPlainString(),
        updatedAtEpochMillis = updatedAt.toEpochMilli(),
    )
}

fun MonthlyFinanceEntity.toMonthlyFinance(expenses: List<ExpenseEntity>): MonthlyFinance {
    val updatedAtMillis = maxOf(
        updatedAtEpochMillis,
        expenses.maxOfOrNull { it.updatedAtEpochMillis } ?: updatedAtEpochMillis,
    )
    return MonthlyFinance(
        userId = userId,
        monthlyIncome = BigDecimal(monthlyIncome),
        expenses = expenses.map { entity ->
            ExpenseEntry(
                categoryKey = entity.categoryKey,
                amount = BigDecimal(entity.amount),
            )
        },
        updatedAt = Instant.ofEpochMilli(updatedAtMillis),
    )
}

fun List<ExpenseEntity>.toMonthlyFinance(userId: String, monthlyIncome: BigDecimal): MonthlyFinance {
    val updatedAt = maxOfOrNull { it.updatedAtEpochMillis }?.let(Instant::ofEpochMilli) ?: Instant.EPOCH
    return MonthlyFinance(
        userId = userId,
        monthlyIncome = monthlyIncome,
        expenses = map { entity ->
            ExpenseEntry(
                categoryKey = entity.categoryKey,
                amount = BigDecimal(entity.amount),
            )
        },
        updatedAt = updatedAt,
    )
}

fun SavedRecommendation.toEntity(): RecommendationEntity {
    return RecommendationEntity(
        id = id,
        userId = userId,
        source = source,
        rationale = rationale,
        createdAtEpochMillis = createdAt.toEpochMilli(),
    )
}

fun RecommendationEntity.toDomain(): SavedRecommendation {
    return SavedRecommendation(
        id = id,
        userId = userId,
        source = source,
        rationale = rationale,
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    )
}

fun ProgressLog.toEntity(): ProgressLogEntity {
    return ProgressLogEntity(
        id = id,
        userId = userId,
        recommendationId = recommendationId,
        actionType = actionType.name,
        amount = amount?.toPlainString(),
        note = note,
        proofReference = proofReference,
        verificationStatus = verificationStatus.name,
        createdAtEpochMillis = createdAt.toEpochMilli(),
    )
}

fun ProgressLogEntity.toDomain(): ProgressLog {
    return ProgressLog(
        id = id,
        userId = userId,
        recommendationId = recommendationId,
        actionType = enumValueOf(actionType),
        amount = amount?.let(::BigDecimal),
        note = note,
        proofReference = proofReference,
        verificationStatus = enumValueOf(verificationStatus),
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    )
}

fun RewardEntity.toDomain(): RewardCatalogueItem {
    return RewardCatalogueItem(
        id = id,
        merchant = merchant,
        title = title,
        pointCost = pointCost,
        isAvailable = isAvailable,
    )
}

fun PointTransaction.toEntity(): PointTransactionEntity {
    return PointTransactionEntity(
        id = id,
        userId = userId,
        points = points,
        type = type.name,
        reason = reason,
        linkedProgressLogId = linkedProgressLogId,
        linkedRewardId = linkedRewardId,
        createdAtEpochMillis = createdAt.toEpochMilli(),
    )
}

fun PointTransactionEntity.toDomain(): PointTransaction {
    return PointTransaction(
        id = id,
        userId = userId,
        points = points,
        type = enumValueOf<PointTransactionType>(type),
        reason = reason,
        linkedProgressLogId = linkedProgressLogId,
        linkedRewardId = linkedRewardId,
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    )
}

private fun String.toAnswerMap(): Map<String, String> {
    if (isBlank()) return emptyMap()
    val json = JSONObject(this)
    return json.keys().asSequence().associateWith { key -> json.getString(key) }
}

private fun Map<String, String>.toJsonString(): String {
    val json = JSONObject()
    entries.sortedBy { it.key }.forEach { (key, value) -> json.put(key, value) }
    return json.toString()
}

private fun FinancialProfile.toAnswerMap(): Map<String, String> {
    return buildMap {
        putAll(answers)
        putIfPresent(FinancialProfileFields.EMPLOYMENT_STATUS, employmentStatus)
        putIfPresent(FinancialProfileFields.INCOME_STABILITY, incomeStability)
        putIfPresent(FinancialProfileFields.DEBT_STATUS, debtStatus)
        putIfPresent(FinancialProfileFields.DEBT_TYPES, debtType)
        putIfPresent(FinancialProfileFields.EMERGENCY_SAVINGS_STATUS, emergencySavingsStatus)
        putIfPresent(FinancialProfileFields.EMERGENCY_SAVINGS_COVERAGE, emergencySavingsCoverageMonths)
        putIfPresent(FinancialProfileFields.MAIN_FINANCIAL_GOALS, mainFinancialGoals)
        putIfPresent(FinancialProfileFields.SHORT_TERM_PURCHASE_GOAL, shortTermPurchaseGoal)
        putIfPresent(FinancialProfileFields.RISK_TOLERANCE, riskTolerance)
        putIfPresent(FinancialProfileFields.BUDGETING_PREFERENCE, budgetingPreference)
        putIfPresent(FinancialProfileFields.UPCOMING_MAJOR_EXPENSES, upcomingMajorExpenses)
    }
}

private fun MutableMap<String, String>.putIfPresent(key: String, value: String?) {
    val normalizedValue = value.normalized()
    if (normalizedValue != null) {
        put(key, normalizedValue)
    }
}

private fun String?.normalized(): String? {
    return this?.trim()?.takeIf { it.isNotBlank() }
}
