package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "point_transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId"), Index("linkedProgressLogId"), Index("linkedRewardId")],
)
data class PointTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val points: Int,
    val type: String,
    val reason: String,
    val linkedProgressLogId: String?,
    val linkedRewardId: String?,
    val createdAtEpochMillis: Long,
)
