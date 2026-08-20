package com.finly.core.domain.model

enum class GoalType {
    EMERGENCY_FUND,
    PURCHASE,
    CUSTOM
}

data class Goal(
    val id: String,
    val title: String,
    val type: GoalType,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: Long,
    val linkedAccountBalance: Double? = null,
    val aiProjectedCompletionDate: Long? = null
) {
    val progressPercentage: Float
        get() = if (targetAmount > 0) ((currentAmount / targetAmount) * 100).toFloat().coerceIn(0f, 100f) else 0f
}
