package com.finly.core.domain.repository

import com.finly.core.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun insertTransactions(transactions: List<Transaction>)
    suspend fun updateCategory(transactionId: String, newCategoryId: String)
    suspend fun deleteTransaction(id: String)
    suspend fun deleteAllTransactions()
    suspend fun clearAllLocalData()
}
