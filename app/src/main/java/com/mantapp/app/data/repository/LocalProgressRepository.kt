package com.mantapp.app.data.repository

import com.mantapp.app.data.local.LocalMantappStore
import com.mantapp.app.domain.model.ProgressLog
import com.mantapp.app.domain.repository.ProgressRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LocalProgressRepository @Inject constructor(
    private val store: LocalMantappStore,
) : ProgressRepository {
    override fun observeProgressLogs(userId: String): Flow<List<ProgressLog>> {
        return store.progressLogs.map { logs -> logs.filter { it.userId == userId } }
    }

    override suspend fun saveProgressLog(log: ProgressLog) {
        store.progressLogs.update { current -> listOf(log) + current }
    }
}
