package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val onboardingComplete: Boolean,
    val createdAtEpochMillis: Long,
)
