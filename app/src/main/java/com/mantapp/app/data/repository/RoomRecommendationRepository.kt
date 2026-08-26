package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.RecommendationDao
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.domain.model.SavedRecommendation
import com.mantapp.app.domain.repository.RecommendationRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomRecommendationRepository @Inject constructor(
    private val recommendationDao: RecommendationDao,
) : RecommendationRepository {
    override fun observeLatestRecommendation(userId: String): Flow<SavedRecommendation?> {
        return recommendationDao.observeLatestForUser(userId).map { it?.toDomain() }
    }

    override suspend fun saveRecommendation(recommendation: SavedRecommendation) {
        withContext(Dispatchers.IO) {
            recommendationDao.upsert(recommendation.toEntity())
        }
    }
}
