package com.mantapp.app.data.repository

import com.mantapp.app.data.local.dao.ProgressLogDao
import com.mantapp.app.data.mapper.toDomain
import com.mantapp.app.data.mapper.toEntity
import com.mantapp.app.domain.model.ProgressLog
import com.mantapp.app.domain.repository.ProgressRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomProgressRepository @Inject constructor(
    private val progressLogDao: ProgressLogDao,
) : ProgressRepository {
    override fun observeProgressLogs(userId: String): Flow<List<ProgressLog>> {
        return progressLogDao.observeByUserId(userId).map { logs -> logs.map { it.toDomain() } }
    }

    override suspend fun saveProgressLog(log: ProgressLog) {
        withContext(Dispatchers.IO) {
            progressLogDao.upsert(log.toEntity())
        }
    }
}
