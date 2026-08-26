package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY pointCost, merchant")
    fun observeAll(): Flow<List<RewardEntity>>

    @Query("SELECT COUNT(*) FROM rewards")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(rewards: List<RewardEntity>): List<Long>
}
