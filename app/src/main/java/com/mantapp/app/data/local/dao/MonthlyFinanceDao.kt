package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.MonthlyFinanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyFinanceDao {
    @Query("SELECT * FROM monthly_finances WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<MonthlyFinanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(finance: MonthlyFinanceEntity): Long
}
