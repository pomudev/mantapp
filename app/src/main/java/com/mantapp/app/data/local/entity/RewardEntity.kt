package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: String,
    val merchant: String,
    val title: String,
    val pointCost: Int,
    val isAvailable: Boolean,
)
