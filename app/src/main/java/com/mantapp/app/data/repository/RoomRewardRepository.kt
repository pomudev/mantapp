package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.PointTransactionDao
import com.mantapp.app.data.local.dao.RewardDao
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.RewardCatalogueItem
import com.mantapp.app.domain.repository.RewardRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomRewardRepository @Inject constructor(
    private val rewardDao: RewardDao,
    private val pointTransactionDao: PointTransactionDao,
) : RewardRepository {
    override fun observeRewardCatalogue(): Flow<List<RewardCatalogueItem>> {
        return rewardDao.observeAll().map { rewards -> rewards.map { it.toDomain() } }
    }

    override fun observePointTransactions(userId: String): Flow<List<PointTransaction>> {
        return pointTransactionDao.observeByUserId(userId).map { transactions ->
            transactions.map { it.toDomain() }
        }
    }

    override suspend fun savePointTransaction(transaction: PointTransaction) {
        withContext(Dispatchers.IO) {
            pointTransactionDao.upsert(transaction.toEntity())
        }
    }
}
