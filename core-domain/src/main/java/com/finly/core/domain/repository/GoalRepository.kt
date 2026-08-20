package com.finly.core.domain.repository

import com.finly.core.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: String): Goal?
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoalProgress(goalId: String, currentAmount: Double)
    suspend fun deleteGoal(id: String)
}
