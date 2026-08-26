package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = 0")
    fun observeSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM session WHERE id = 0")
    fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(session: SessionEntity): Long
}
