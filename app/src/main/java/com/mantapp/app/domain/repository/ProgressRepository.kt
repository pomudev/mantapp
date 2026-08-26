package com.mantapp.app.domain.repository

import com.mantapp.app.domain.model.ProgressLog
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgressLogs(userId: String): Flow<List<ProgressLog>>

    suspend fun saveProgressLog(log: ProgressLog)
}
