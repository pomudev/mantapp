package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.FinancialProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialProfileDao {
    @Query("SELECT * FROM financial_profiles WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<FinancialProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(profile: FinancialProfileEntity): Long
}
