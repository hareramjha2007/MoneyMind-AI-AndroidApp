package com.finly.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finly.core.data.entity.FinancialHealthScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialHealthScoreDao {
    @Query("SELECT * FROM health_scores ORDER BY date DESC LIMIT 1")
    fun getLatestScore(): Flow<FinancialHealthScoreEntity?>

    @Query("SELECT * FROM health_scores ORDER BY date DESC LIMIT :limitDays")
    fun getScoreHistory(limitDays: Int): Flow<List<FinancialHealthScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScore(score: FinancialHealthScoreEntity)

    @Query("DELETE FROM health_scores")
    suspend fun deleteAllScores()
}
