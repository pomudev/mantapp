package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY categoryKey")
    fun observeByUserId(userId: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses WHERE userId = :userId")
    fun deleteForUser(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(expenses: List<ExpenseEntity>): List<Long>
}
