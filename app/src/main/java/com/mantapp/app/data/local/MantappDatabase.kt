package com.mantapp.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1,
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
}
