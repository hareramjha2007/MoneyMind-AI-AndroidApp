package com.finly.core.domain.repository

import com.finly.core.domain.model.CoachMessage
import kotlinx.coroutines.flow.Flow

interface CoachRepository {
    fun getMessagesForSession(sessionId: String): Flow<List<CoachMessage>>
    suspend fun saveMessage(message: CoachMessage)
    suspend fun clearSession(sessionId: String)
}
