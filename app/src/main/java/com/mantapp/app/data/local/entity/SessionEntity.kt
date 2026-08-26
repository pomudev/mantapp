package com.mantapp.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val id: Int = ACTIVE_SESSION_ID,
    val activeUserId: String?,
    val isOnboardingComplete: Boolean,
) {
    companion object {
        const val ACTIVE_SESSION_ID = 0
    }
}
