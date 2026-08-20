package com.finly.core.domain.repository

import com.finly.core.domain.model.FinancialHealthScore
import kotlinx.coroutines.flow.Flow

interface HealthScoreRepository {
    fun getLatestScore(): Flow<FinancialHealthScore?>
    fun getScoreHistory(limitDays: Int): Flow<List<FinancialHealthScore>>
    suspend fun saveScore(score: FinancialHealthScore)
}
