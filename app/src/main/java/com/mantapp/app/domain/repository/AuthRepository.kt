package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.SessionState
import com.mantapp.app.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<SessionState>

    suspend fun register(displayName: String, email: String, password: String): UserAccount

    suspend fun login(email: String, password: String): UserAccount?

    suspend fun updateOnboardingComplete(isComplete: Boolean)

    suspend fun logout()
}
