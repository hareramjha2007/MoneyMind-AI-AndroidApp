package com.finly.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finly.core.data.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("UPDATE goals SET currentAmount = :currentAmount WHERE id = :goalId")
    suspend fun updateGoalProgress(goalId: String, currentAmount: Double)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: String)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}
