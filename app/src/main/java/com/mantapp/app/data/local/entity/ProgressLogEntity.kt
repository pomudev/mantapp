package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "progress_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId"), Index("recommendationId")],
)
data class ProgressLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val recommendationId: String?,
    val actionType: String,
    val amount: String?,
    val note: String,
    val proofReference: String?,
    val verificationStatus: String,
    val createdAtEpochMillis: Long,
)
