package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.model.TransactionDirection

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val direction: String, // DEBIT, CREDIT
    val timestamp: Long,
    val sourceApp: String,
    val rawSenderId: String,
    val categoryId: String,
    val merchant: String?,
    val isRecurring: Boolean,
    val confidenceScore: Float,
    val userCorrected: Boolean
) {
    fun toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            direction = TransactionDirection.valueOf(direction),
            timestamp = timestamp,
            sourceApp = sourceApp,
            rawSenderId = rawSenderId,
            categoryId = categoryId,
            merchant = merchant,
            isRecurring = isRecurring,
            confidenceScore = confidenceScore,
            userCorrected = userCorrected
        )
    }

    companion object {
        fun fromDomain(domain: Transaction): TransactionEntity {
            return TransactionEntity(
                id = domain.id,
                amount = domain.amount,
                direction = domain.direction.name,
                timestamp = domain.timestamp,
                sourceApp = domain.sourceApp,
                rawSenderId = domain.rawSenderId,
                categoryId = domain.categoryId,
                merchant = domain.merchant,
                isRecurring = domain.isRecurring,
                confidenceScore = domain.confidenceScore,
                userCorrected = domain.userCorrected
            )
        }
    }
}
