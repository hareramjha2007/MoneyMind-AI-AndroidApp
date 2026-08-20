package com.finly.core.data.repository

import com.finly.core.data.dao.GoalDao
import com.finly.core.data.entity.GoalEntity
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalType
import com.finly.core.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> {
        return dao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getGoalById(id: String): Goal? {
        return dao.getGoalById(id)?.toDomain()
    }

    override suspend fun insertGoal(goal: Goal) {
        dao.insertGoal(GoalEntity.fromDomain(goal))
    }

    override suspend fun updateGoalProgress(goalId: String, currentAmount: Double) {
        dao.updateGoalProgress(goalId, currentAmount)
    }

    override suspend fun deleteGoal(id: String) {
        dao.deleteGoal(id)
    }
}
