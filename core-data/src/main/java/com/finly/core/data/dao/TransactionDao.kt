package com.finly.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finly.core.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("UPDATE transactions SET categoryId = :newCategoryId, userCorrected = 1 WHERE id = :transactionId")
    suspend fun updateCategory(transactionId: String, newCategoryId: String)

    @Query("UPDATE transactions SET categoryId = :categoryId, isExcludedFromExpenses = :isExcluded, notes = :notes, userCorrected = 1 WHERE id = :id")
    suspend fun updateTransactionDetails(id: String, categoryId: String, isExcluded: Boolean, notes: String?)

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getSyncTransactionsBetween(startTime: Long, endTime: Long): List<TransactionEntity>

    @Query("UPDATE transactions SET merchant = :merchant, categoryId = :categoryId WHERE id = :id")
    suspend fun updateMerchantAndCategory(id: String, merchant: String, categoryId: String)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
