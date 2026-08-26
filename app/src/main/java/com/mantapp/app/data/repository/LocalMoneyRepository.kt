package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.repository.MoneyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LocalMoneyRepository @Inject constructor(
    private val store: LocalMantappStore,
) : MoneyRepository {
    override fun observeMonthlyFinance(userId: String): Flow<MonthlyFinance?> {
        return store.monthlyFinances.map { finances -> finances[userId] }
    }

    override suspend fun saveMonthlyFinance(finance: MonthlyFinance) {
        store.monthlyFinances.update { current -> current + (finance.userId to finance) }
    }
}
