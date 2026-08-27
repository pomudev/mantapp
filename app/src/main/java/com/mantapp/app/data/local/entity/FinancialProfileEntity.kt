package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_profiles",
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
data class FinancialProfileEntity(
    @PrimaryKey val userId: String,
    val answersJson: String,
    val employmentStatus: String?,
    val incomeStability: String?,
    val debtStatus: String?,
    val debtType: String?,
    val emergencySavingsStatus: String?,
    val emergencySavingsCoverageMonths: String?,
    val mainFinancialGoals: String?,
    val shortTermPurchaseGoal: String?,
    val riskTolerance: String?,
    val budgetingPreference: String?,
    val upcomingMajorExpenses: String?,
    val completedAtEpochMillis: Long?,
)
