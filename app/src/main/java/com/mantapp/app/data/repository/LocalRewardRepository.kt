package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.RewardCatalogueItem
import com.mantapp.app.domain.repository.RewardRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LocalRewardRepository @Inject constructor(
    private val store: LocalMantappStore,
) : RewardRepository {
    override fun observeRewardCatalogue(): Flow<List<RewardCatalogueItem>> {
        return store.rewards
    }

    override fun observePointTransactions(userId: String): Flow<List<PointTransaction>> {
        return store.pointTransactions.map { transactions ->
            transactions.filter { it.userId == userId }
        }
    }

    override suspend fun savePointTransaction(transaction: PointTransaction) {
        store.pointTransactions.update { current -> current + transaction }
    }
}
