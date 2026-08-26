package com.mantapp.app.data.repository

import androidx.room.withTransaction
import com.mantapp.app.data.local.MantappDatabase
import com.mantapp.app.data.local.dao.ExpenseDao
import com.mantapp.app.data.local.dao.MonthlyFinanceDao
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.data.mapper.toExpenseEntities
import com.mantapp.app.data.mapper.toMonthlyFinance
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.repository.MoneyRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class RoomMoneyRepository @Inject constructor(
    private val database: MantappDatabase,
    private val monthlyFinanceDao: MonthlyFinanceDao,
    private val expenseDao: ExpenseDao,
) : MoneyRepository {
    override fun observeMonthlyFinance(userId: String): Flow<MonthlyFinance?> {
        return combine(
            monthlyFinanceDao.observeByUserId(userId),
            expenseDao.observeByUserId(userId),
        ) { finance, expenses ->
            finance?.toMonthlyFinance(expenses)
        }
    }

    override suspend fun saveMonthlyFinance(finance: MonthlyFinance) {
        withContext(Dispatchers.IO) {
            database.withTransaction {
                monthlyFinanceDao.upsert(finance.toEntity())
                expenseDao.deleteForUser(finance.userId)
                expenseDao.insertAll(finance.toExpenseEntities())
            }
        }
    }
}
