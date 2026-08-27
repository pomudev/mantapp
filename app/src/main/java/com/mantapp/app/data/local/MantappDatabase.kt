package com.mantapp.app.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mantapp.app.data.local.dao.ExpenseDao
import com.mantapp.app.data.local.dao.FinancialProfileDao
import com.mantapp.app.data.local.dao.MonthlyFinanceDao
import com.mantapp.app.data.local.dao.PointTransactionDao
import com.mantapp.app.data.local.dao.ProgressLogDao
import com.mantapp.app.data.local.dao.RecommendationDao
import com.mantapp.app.data.local.dao.RewardDao
import com.mantapp.app.data.local.dao.SessionDao
import com.mantapp.app.data.local.dao.UserDao
import com.mantapp.app.data.local.entity.ExpenseEntity
import com.mantapp.app.data.local.entity.FinancialProfileEntity
import com.mantapp.app.data.local.entity.MonthlyFinanceEntity
import com.mantapp.app.data.local.entity.PointTransactionEntity
import com.mantapp.app.data.local.entity.ProgressLogEntity
import com.mantapp.app.data.local.entity.RecommendationEntity
import com.mantapp.app.data.local.entity.RewardEntity
import com.mantapp.app.data.local.entity.SessionEntity
import com.mantapp.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
        FinancialProfileEntity::class,
        MonthlyFinanceEntity::class,
        ExpenseEntity::class,
        RecommendationEntity::class,
        ProgressLogEntity::class,
        RewardEntity::class,
        PointTransactionEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class MantappDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun financialProfileDao(): FinancialProfileDao
    abstract fun monthlyFinanceDao(): MonthlyFinanceDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun recommendationDao(): RecommendationDao
    abstract fun progressLogDao(): ProgressLogDao
    abstract fun rewardDao(): RewardDao
    abstract fun pointTransactionDao(): PointTransactionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN employmentStatus TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN incomeStability TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN debtStatus TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN debtType TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN emergencySavingsStatus TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN emergencySavingsCoverageMonths TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN mainFinancialGoals TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN shortTermPurchaseGoal TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN riskTolerance TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN budgetingPreference TEXT")
                db.execSQL("ALTER TABLE financial_profiles ADD COLUMN upcomingMajorExpenses TEXT")
            }
        }
    }
}
