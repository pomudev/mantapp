package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.SavedRecommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun observeLatestRecommendation(userId: String): Flow<SavedRecommendation?>

    suspend fun saveRecommendation(recommendation: SavedRecommendation)
}
