package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.ProgressLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressLogDao {
    @Query("SELECT * FROM progress_logs WHERE userId = :userId ORDER BY createdAtEpochMillis DESC")
    fun observeByUserId(userId: String): Flow<List<ProgressLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(log: ProgressLogEntity): Long
}
