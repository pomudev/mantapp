package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.FinancialProfileDao
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.repository.FinancialProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomFinancialProfileRepository @Inject constructor(
    private val financialProfileDao: FinancialProfileDao,
) : FinancialProfileRepository {
    override fun observeProfile(userId: String): Flow<FinancialProfile?> {
        return financialProfileDao.observeByUserId(userId).map { it?.toDomain() }
    }

    override suspend fun saveProfile(profile: FinancialProfile) {
        withContext(Dispatchers.IO) {
            financialProfileDao.upsert(profile.toEntity())
        }
    }
}
