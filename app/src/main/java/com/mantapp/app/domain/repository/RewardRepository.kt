package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.RewardCatalogueItem
import kotlinx.coroutines.flow.Flow

interface RewardRepository {
    fun observeRewardCatalogue(): Flow<List<RewardCatalogueItem>>

    fun observePointTransactions(userId: String): Flow<List<PointTransaction>>

    suspend fun savePointTransaction(transaction: PointTransaction)
}
