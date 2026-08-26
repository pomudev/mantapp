package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.SavedRecommendation
import com.mantapp.app.domain.repository.RecommendationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LocalRecommendationRepository @Inject constructor(
    private val store: LocalMantappStore,
) : RecommendationRepository {
    override fun observeLatestRecommendation(userId: String): Flow<SavedRecommendation?> {
        return store.recommendations.map { recommendations ->
            recommendations
                .filter { it.userId == userId }
                .maxByOrNull { it.createdAt }
        }
    }

    override suspend fun saveRecommendation(recommendation: SavedRecommendation) {
        store.recommendations.update { current -> current + recommendation }
    }
}
