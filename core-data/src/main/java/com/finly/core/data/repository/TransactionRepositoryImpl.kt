package com.finly.core.data.repository

import com.finly.core.data.dao.CoachMessageDao
import com.finly.core.data.dao.FinancialHealthScoreDao
import com.finly.core.data.dao.GoalDao
import com.finly.core.data.dao.TransactionDao
import com.finly.core.data.entity.TransactionEntity
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    private val goalDao: GoalDao,
    private val healthScoreDao: FinancialHealthScoreDao,
    private val coachMessageDao: CoachMessageDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return dao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<Transaction>> {
        return dao.getTransactionsBetween(startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun insertTransactions(transactions: List<Transaction>) {
        dao.insertTransactions(transactions.map { TransactionEntity.fromDomain(it) })
    }

    override suspend fun updateCategory(transactionId: String, newCategoryId: String) {
        dao.updateCategory(transactionId, newCategoryId)
    }

    override suspend fun deleteTransaction(id: String) {
        dao.deleteTransaction(id)
    }

    override suspend fun deleteAllTransactions() {
        dao.deleteAllTransactions()
    }

    override suspend fun clearAllLocalData() {
        dao.deleteAllTransactions()
        goalDao.deleteAllGoals()
        healthScoreDao.deleteAllScores()
        coachMessageDao.deleteAllMessages()
    }
}
