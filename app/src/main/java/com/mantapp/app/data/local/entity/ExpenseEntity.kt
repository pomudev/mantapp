package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "expenses",
    primaryKeys = ["userId", "categoryKey"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId")],
)
data class ExpenseEntity(
    val userId: String,
    val categoryKey: String,
    val amount: String,
    val updatedAtEpochMillis: Long,
)
