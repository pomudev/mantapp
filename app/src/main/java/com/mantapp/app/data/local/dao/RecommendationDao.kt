package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.RecommendationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {
    @Query(
        "SELECT * FROM recommendations WHERE userId = :userId " +
            "ORDER BY createdAtEpochMillis DESC LIMIT 1",
    )
    fun observeLatestForUser(userId: String): Flow<RecommendationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(recommendation: RecommendationEntity): Long
}
