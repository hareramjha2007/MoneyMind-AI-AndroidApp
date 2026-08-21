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
        val windowMs = 5 * 60 * 1000L // 5 minute time window
        val startTime = transaction.timestamp - windowMs
        val endTime = transaction.timestamp + windowMs

        val recentTransactions = dao.getSyncTransactionsBetween(startTime, endTime)

        // 1. Check for exact amount match within 5 minutes across channels (Bank SMS, UPI app, Axio)
        val existingDuplicate = recentTransactions.firstOrNull { existing ->
            Math.abs(existing.amount - transaction.amount) < 0.05 &&
                    existing.direction == transaction.direction.name
        }

        if (existingDuplicate != null) {
            // DUPLICATE DETECTED! Enrich existing transaction if new record has better merchant details!
            val newMerchant = transaction.merchant
            val existingMerch = existingDuplicate.merchant.orEmpty()
            if (!newMerchant.isNullOrBlank() && (existingMerch.isBlank() || existingMerch.contains("walnut") || existingMerch.contains("com.") || existingMerch == "others")) {
                dao.updateMerchantAndCategory(
                    id = existingDuplicate.id,
                    merchant = newMerchant,
                    categoryId = transaction.categoryId
                )
            }
            return // Skip duplicate insertion!
        }

        // 2. Check for double-calculated sum notifications (e.g. ₹17,672 summary right after 2x ₹8,836 deductions)
        val sumRecent = recentTransactions.sumOf { it.amount }
        if (recentTransactions.size >= 2 && Math.abs(sumRecent - transaction.amount) < 0.5) {
            return // Skip duplicate summary notification!
        }

        dao.insertTransaction(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun insertTransactions(transactions: List<Transaction>) {
        dao.insertTransactions(transactions.map { TransactionEntity.fromDomain(it) })
    }

    override suspend fun updateCategory(transactionId: String, newCategoryId: String) {
        dao.updateCategory(transactionId, newCategoryId)
    }

    override suspend fun updateTransactionDetails(id: String, categoryId: String, isExcludedFromExpenses: Boolean, notes: String?) {
        dao.updateTransactionDetails(id, categoryId, isExcludedFromExpenses, notes)
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
