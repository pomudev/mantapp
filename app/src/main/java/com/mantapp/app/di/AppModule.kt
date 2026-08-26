package com.mantapp.app.di

import com.mantapp.app.data.repository.LocalAuthRepository
import com.mantapp.app.data.repository.LocalFinancialProfileRepository
import com.mantapp.app.data.repository.LocalMoneyRepository
import com.mantapp.app.data.repository.LocalProgressRepository
import com.mantapp.app.data.repository.LocalRecommendationRepository
import com.mantapp.app.data.repository.LocalRewardRepository
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
    abstract fun bindAuthRepository(repository: LocalAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFinancialProfileRepository(
        repository: LocalFinancialProfileRepository,
    ): FinancialProfileRepository

    @Binds
    @Singleton
    abstract fun bindMoneyRepository(repository: LocalMoneyRepository): MoneyRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        repository: LocalRecommendationRepository,
    ): RecommendationRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(repository: LocalProgressRepository): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindRewardRepository(repository: LocalRewardRepository): RewardRepository
}
