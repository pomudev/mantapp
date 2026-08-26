package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.SessionDao
import com.mantapp.app.data.local.dao.UserDao
import com.mantapp.app.data.local.entity.SessionEntity
import com.mantapp.app.data.local.entity.UserEntity
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.security.PasswordHasher
import com.mantapp.app.domain.model.SessionState
import com.mantapp.app.domain.model.UserAccount
import com.mantapp.app.domain.repository.AuthRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomAuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val passwordHasher: PasswordHasher,
) : AuthRepository {
    override val session: Flow<SessionState> = sessionDao.observeSession().map { entity ->
        entity?.toDomain() ?: SessionState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val activeUser: Flow<UserAccount?> = sessionDao.observeSession().flatMapLatest { session ->
        val activeUserId = session?.activeUserId
        if (activeUserId == null) {
            flowOf(null)
        } else {
            userDao.observeById(activeUserId).map { it?.toDomain() }
        }
    }

    override suspend fun register(
        displayName: String,
        email: String,
        password: String,
    ): UserAccount = withContext(Dispatchers.IO) {
        require(userDao.getByEmail(email) == null) { "An account already exists for this email." }

        val passwordHash = passwordHasher.createHash(password)
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            displayName = displayName.trim(),
            email = email.trim(),
            passwordHash = passwordHash.hash,
            passwordSalt = passwordHash.salt,
            onboardingComplete = false,
            createdAtEpochMillis = Instant.now().toEpochMilli(),
        )
        userDao.insert(entity)
        sessionDao.upsert(
            SessionEntity(
                activeUserId = entity.id,
                isOnboardingComplete = false,
            ),
        )
        entity.toDomain()
    }

    override suspend fun login(email: String, password: String): UserAccount? {
        return withContext(Dispatchers.IO) {
            val user = userDao.getByEmail(email) ?: return@withContext null
            if (!passwordHasher.matches(password, user.passwordHash, user.passwordSalt)) {
                return@withContext null
            }
            sessionDao.upsert(
                SessionEntity(
                    activeUserId = user.id,
                    isOnboardingComplete = user.onboardingComplete,
                ),
            )
            user.toDomain()
        }
    }

    override suspend fun updateOnboardingComplete(isComplete: Boolean) {
        withContext(Dispatchers.IO) {
            val session = sessionDao.getSession() ?: return@withContext
            val activeUserId = session.activeUserId ?: return@withContext
            userDao.updateOnboardingComplete(activeUserId, isComplete)
            sessionDao.upsert(session.copy(isOnboardingComplete = isComplete))
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            sessionDao.upsert(
                SessionEntity(
                    activeUserId = null,
                    isOnboardingComplete = false,
                ),
            )
        }
    }
}

private fun SessionEntity.toDomain(): SessionState {
    return SessionState(
        activeUserId = activeUserId,
        isOnboardingComplete = isOnboardingComplete,
    )
}
