package com.finly.core.domain.model

enum class CoachSender {
    USER,
    COACH,
    SYSTEM
}

data class CoachMessage(
    val id: String,
    val sessionId: String,
    val sender: CoachSender,
    val text: String,
    val timestamp: Long,
    val contextSnapshotJson: String? = null
)
