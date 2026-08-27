package com.mantapp.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mantapp.app.data.local.MantappDatabase
import com.mantapp.app.data.local.dao.ExpenseDao
import com.mantapp.app.data.local.dao.FinancialProfileDao
import com.mantapp.app.data.local.dao.MonthlyFinanceDao
import com.mantapp.app.data.local.dao.PointTransactionDao
import com.mantapp.app.data.local.dao.ProgressLogDao
import com.mantapp.app.data.local.dao.RecommendationDao
import com.mantapp.app.data.local.dao.RewardDao
import com.mantapp.app.data.local.dao.SessionDao
import com.mantapp.app.data.local.dao.UserDao
import com.mantapp.app.data.local.defaultRewardEntities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMantappDatabase(
        @ApplicationContext context: Context,
    ): MantappDatabase {
        return Room.databaseBuilder(
            context,
            MantappDatabase::class.java,
            "mantapp.db",
        )
            .addCallback(RewardSeedCallback())
            .addMigrations(MantappDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideUserDao(database: MantappDatabase): UserDao = database.userDao()

    @Provides
    fun provideSessionDao(database: MantappDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideFinancialProfileDao(database: MantappDatabase): FinancialProfileDao {
        return database.financialProfileDao()
    }

    @Provides
    fun provideMonthlyFinanceDao(database: MantappDatabase): MonthlyFinanceDao {
        return database.monthlyFinanceDao()
    }

    @Provides
    fun provideExpenseDao(database: MantappDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideRecommendationDao(database: MantappDatabase): RecommendationDao {
        return database.recommendationDao()
    }

    @Provides
    fun provideProgressLogDao(database: MantappDatabase): ProgressLogDao {
        return database.progressLogDao()
    }

    @Provides
    fun provideRewardDao(database: MantappDatabase): RewardDao = database.rewardDao()

    @Provides
    fun providePointTransactionDao(database: MantappDatabase): PointTransactionDao {
        return database.pointTransactionDao()
    }
}

private class RewardSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        defaultRewardEntities().forEach { reward ->
            db.execSQL(
                "INSERT OR IGNORE INTO rewards(id, merchant, title, pointCost, isAvailable) " +
                    "VALUES(?, ?, ?, ?, ?)",
                arrayOf<Any>(
                    reward.id,
                    reward.merchant,
                    reward.title,
                    reward.pointCost,
                    if (reward.isAvailable) 1 else 0,
                ),
            )
        }
    }
}
