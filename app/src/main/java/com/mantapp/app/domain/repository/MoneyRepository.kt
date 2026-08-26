package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.MonthlyFinance
import kotlinx.coroutines.flow.Flow

interface MoneyRepository {
    fun observeMonthlyFinance(userId: String): Flow<MonthlyFinance?>

    suspend fun saveMonthlyFinance(finance: MonthlyFinance)
}
