package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.FinancialProfileDao
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.domain.model.FinancialProfileFields
import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.model.FinancialProfileValidationResult
import com.mantapp.app.domain.repository.FinancialProfileRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomFinancialProfileRepository @Inject constructor(
    private val financialProfileDao: FinancialProfileDao,
) : FinancialProfileRepository {
    override fun observeProfile(userId: String): Flow<FinancialProfile?> {
        return financialProfileDao.observeByUserId(userId).map { it?.toDomain() }
    }

    override fun validateOnboardingAnswers(answers: Map<String, String>): FinancialProfileValidationResult {
        val debtStatus = answers[FinancialProfileFields.DEBT_STATUS].orEmpty()
        val requiredKeys = if (debtStatus == NO_DEBT_ANSWER) {
            requiredOnboardingKeys - FinancialProfileFields.DEBT_TYPES
        } else {
            requiredOnboardingKeys
        }
        return FinancialProfileValidationResult(
            missingFieldKeys = requiredKeys.filter { key -> answers[key].isNullOrBlank() },
        )
    }

    override fun createProfileFromOnboardingAnswers(
        userId: String,
        answers: Map<String, String>,
        completedAtEpochMillis: Long,
    ): FinancialProfile {
        val validation = validateOnboardingAnswers(answers)
        require(validation.isValid) {
            "Missing required financial profile answers: ${validation.missingFieldKeys.joinToString()}"
        }
        return answers.toFinancialProfile(
            userId = userId,
            completedAt = Instant.ofEpochMilli(completedAtEpochMillis),
        )
    }

    override suspend fun saveProfile(profile: FinancialProfile) {
        withContext(Dispatchers.IO) {
            financialProfileDao.upsert(profile.toEntity())
        }
    }

    override suspend fun updateProfileFields(userId: String, answers: Map<String, String>) {
        withContext(Dispatchers.IO) {
            val existing = financialProfileDao.observeByUserId(userId).first()?.toDomain()
            val mergedAnswers = existing?.answers.orEmpty() + answers
            val completedAt = existing?.completedAt ?: Instant.now()
            financialProfileDao.upsert(
                mergedAnswers.toFinancialProfile(
                    userId = userId,
                    completedAt = completedAt,
                ).toEntity(),
            )
        }
    }

    private fun Map<String, String>.toFinancialProfile(
        userId: String,
        completedAt: Instant,
    ): FinancialProfile {
        return FinancialProfile(
            userId = userId,
            employmentStatus = this[FinancialProfileFields.EMPLOYMENT_STATUS],
            incomeStability = this[FinancialProfileFields.INCOME_STABILITY],
            debtStatus = this[FinancialProfileFields.DEBT_STATUS],
            debtType = this[FinancialProfileFields.DEBT_TYPES],
            emergencySavingsStatus = this[FinancialProfileFields.EMERGENCY_SAVINGS_STATUS],
            emergencySavingsCoverageMonths = this[FinancialProfileFields.EMERGENCY_SAVINGS_COVERAGE],
            mainFinancialGoals = this[FinancialProfileFields.MAIN_FINANCIAL_GOALS],
            shortTermPurchaseGoal = this[FinancialProfileFields.SHORT_TERM_PURCHASE_GOAL],
            riskTolerance = this[FinancialProfileFields.RISK_TOLERANCE],
            budgetingPreference = this[FinancialProfileFields.BUDGETING_PREFERENCE],
            upcomingMajorExpenses = this[FinancialProfileFields.UPCOMING_MAJOR_EXPENSES],
            answers = this,
            completedAt = completedAt,
        )
    }

    private companion object {
        const val NO_DEBT_ANSWER = "No debt right now"

        val requiredOnboardingKeys = listOf(
            FinancialProfileFields.EMPLOYMENT_STATUS,
            FinancialProfileFields.INCOME_STABILITY,
            FinancialProfileFields.DEBT_STATUS,
            FinancialProfileFields.DEBT_TYPES,
            FinancialProfileFields.EMERGENCY_SAVINGS_STATUS,
            FinancialProfileFields.EMERGENCY_SAVINGS_COVERAGE,
            FinancialProfileFields.MAIN_FINANCIAL_GOALS,
            FinancialProfileFields.SHORT_TERM_PURCHASE_GOAL,
            FinancialProfileFields.RISK_TOLERANCE,
            FinancialProfileFields.BUDGETING_PREFERENCE,
            FinancialProfileFields.UPCOMING_MAJOR_EXPENSES,
        )
    }
}
