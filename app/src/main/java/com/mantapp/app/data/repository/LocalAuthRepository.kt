package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.SessionState
import com.mantapp.app.domain.model.UserAccount
import com.mantapp.app.domain.repository.AuthRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update

class LocalAuthRepository @Inject constructor(
    private val store: LocalMantappStore,
) : AuthRepository {
    override val session: Flow<SessionState> = store.session

    override suspend fun register(
        displayName: String,
        email: String,
        password: String,
    ): UserAccount {
        val user = UserAccount(
            id = UUID.randomUUID().toString(),
            displayName = displayName.trim(),
            email = email.trim(),
            createdAt = Instant.now(),
        )
        store.users.update { current -> current + user }
        store.session.value = SessionState(activeUserId = user.id)
        return user
    }

    override suspend fun login(email: String, password: String): UserAccount? {
        val user = store.users.value.firstOrNull { account ->
            account.email.equals(email.trim(), ignoreCase = true)
        }
        store.session.value = store.session.value.copy(activeUserId = user?.id)
        return user
    }

    override suspend fun updateOnboardingComplete(isComplete: Boolean) {
        store.session.value = store.session.value.copy(isOnboardingComplete = isComplete)
    }

    override suspend fun logout() {
        store.session.value = SessionState()
    }
}
