package com.mantapp.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mantapp.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE lower(email) = lower(:email) LIMIT 1")
    fun getByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(user: UserEntity): Long

    @Query("UPDATE users SET onboardingComplete = :isComplete WHERE id = :userId")
    fun updateOnboardingComplete(userId: String, isComplete: Boolean): Int
}
