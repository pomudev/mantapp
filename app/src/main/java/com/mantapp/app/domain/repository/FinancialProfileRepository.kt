package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.model.FinancialProfileValidationResult
import kotlinx.coroutines.flow.Flow

interface FinancialProfileRepository {
    fun observeProfile(userId: String): Flow<FinancialProfile?>

    fun validateOnboardingAnswers(answers: Map<String, String>): FinancialProfileValidationResult

    fun createProfileFromOnboardingAnswers(
        userId: String,
        answers: Map<String, String>,
        completedAtEpochMillis: Long,
    ): FinancialProfile

    suspend fun saveProfile(profile: FinancialProfile)

    suspend fun updateProfileFields(userId: String, answers: Map<String, String>)
}
