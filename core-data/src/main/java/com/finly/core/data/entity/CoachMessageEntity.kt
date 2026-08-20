package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.CoachMessage
import com.finly.core.domain.model.CoachSender

@Entity(tableName = "coach_messages")
data class CoachMessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val sender: String, // USER, COACH, SYSTEM
    val text: String,
    val timestamp: Long,
    val contextSnapshotJson: String?
) {
    fun toDomain(): CoachMessage {
        return CoachMessage(
            id = id,
            sessionId = sessionId,
            sender = CoachSender.valueOf(sender),
            text = text,
            timestamp = timestamp,
            contextSnapshotJson = contextSnapshotJson
        )
    }

    companion object {
        fun fromDomain(domain: CoachMessage): CoachMessageEntity {
            return CoachMessageEntity(
                id = domain.id,
                sessionId = domain.sessionId,
                sender = domain.sender.name,
                text = domain.text,
                timestamp = domain.timestamp,
                contextSnapshotJson = domain.contextSnapshotJson
            )
        }
    }
}
