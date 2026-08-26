package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.PointTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointTransactionDao {
    @Query("SELECT * FROM point_transactions WHERE userId = :userId ORDER BY createdAtEpochMillis DESC")
    fun observeByUserId(userId: String): Flow<List<PointTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(transaction: PointTransactionEntity): Long
}
