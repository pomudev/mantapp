package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.FinancialProfile
import kotlinx.coroutines.flow.Flow

interface FinancialProfileRepository {
    fun observeProfile(userId: String): Flow<FinancialProfile?>

    suspend fun saveProfile(profile: FinancialProfile)
}
