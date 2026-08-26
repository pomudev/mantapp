package com.mantapp.app.di

import com.mantapp.app.data.repository.RoomAuthRepository
import com.mantapp.app.data.repository.RoomFinancialProfileRepository
import com.mantapp.app.data.repository.RoomMoneyRepository
import com.mantapp.app.data.repository.RoomProgressRepository
import com.mantapp.app.data.repository.RoomRecommendationRepository
import com.mantapp.app.data.repository.RoomRewardRepository
import com.mantapp.app.domain.repository.AuthRepository
import com.mantapp.app.domain.repository.FinancialProfileRepository
import com.mantapp.app.domain.repository.MoneyRepository
import com.mantapp.app.domain.repository.ProgressRepository
import com.mantapp.app.domain.repository.RecommendationRepository
import com.mantapp.app.domain.repository.RewardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: RoomAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFinancialProfileRepository(
        repository: RoomFinancialProfileRepository,
    ): FinancialProfileRepository

    @Binds
    @Singleton
    abstract fun bindMoneyRepository(repository: RoomMoneyRepository): MoneyRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        repository: RoomRecommendationRepository,
    ): RecommendationRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(repository: RoomProgressRepository): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindRewardRepository(repository: RoomRewardRepository): RewardRepository
}
