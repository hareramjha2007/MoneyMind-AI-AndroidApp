package com.finly.core.data.repository

import com.finly.core.data.dao.CoachMessageDao
import com.finly.core.data.entity.CoachMessageEntity
import com.finly.core.domain.model.CoachMessage
import com.finly.core.domain.repository.CoachRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoachRepositoryImpl @Inject constructor(
    private val dao: CoachMessageDao
) : CoachRepository {

    override fun getMessagesForSession(sessionId: String): Flow<List<CoachMessage>> {
        return dao.getMessagesForSession(sessionId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveMessage(message: CoachMessage) {
        dao.saveMessage(CoachMessageEntity.fromDomain(message))
    }

    override suspend fun clearSession(sessionId: String) {
        dao.clearSession(sessionId)
    }
}
