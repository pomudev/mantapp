package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.repository.FinancialProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LocalFinancialProfileRepository @Inject constructor(
    private val store: LocalMantappStore,
) : FinancialProfileRepository {
    override fun observeProfile(userId: String): Flow<FinancialProfile?> {
        return store.profiles.map { profiles -> profiles[userId] }
    }

    override suspend fun saveProfile(profile: FinancialProfile) {
        store.profiles.update { current -> current + (profile.userId to profile) }
    }
}
