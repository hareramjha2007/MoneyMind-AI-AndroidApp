package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalType

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // EMERGENCY_FUND, PURCHASE, CUSTOM
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: Long,
    val linkedAccountBalance: Double?,
    val aiProjectedCompletionDate: Long?
) {
    fun toDomain(): Goal {
        return Goal(
            id = id,
            title = title,
            type = GoalType.valueOf(type),
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            targetDate = targetDate,
            linkedAccountBalance = linkedAccountBalance,
            aiProjectedCompletionDate = aiProjectedCompletionDate
        )
    }

    companion object {
        fun fromDomain(domain: Goal): GoalEntity {
            return GoalEntity(
                id = domain.id,
                title = domain.title,
                type = domain.type.name,
                targetAmount = domain.targetAmount,
                currentAmount = domain.currentAmount,
                targetDate = domain.targetDate,
                linkedAccountBalance = domain.linkedAccountBalance,
                aiProjectedCompletionDate = domain.aiProjectedCompletionDate
            )
        }
    }
}
