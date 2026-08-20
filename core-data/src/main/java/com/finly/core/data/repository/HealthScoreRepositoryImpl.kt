package com.finly.core.data.repository

import com.finly.core.data.dao.FinancialHealthScoreDao
import com.finly.core.data.entity.FinancialHealthScoreEntity
import com.finly.core.domain.model.FinancialHealthScore
import com.finly.core.domain.repository.HealthScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HealthScoreRepositoryImpl @Inject constructor(
    private val dao: FinancialHealthScoreDao
) : HealthScoreRepository {

    override fun getLatestScore(): Flow<FinancialHealthScore?> {
        return dao.getLatestScore().map { it?.toDomain() }
    }

    override fun getScoreHistory(limitDays: Int): Flow<List<FinancialHealthScore>> {
        return dao.getScoreHistory(limitDays).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveScore(score: FinancialHealthScore) {
        dao.saveScore(FinancialHealthScoreEntity.fromDomain(score))
    }
}
