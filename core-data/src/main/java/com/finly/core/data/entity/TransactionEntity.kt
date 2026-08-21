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
    val userCorrected: Boolean,
    val isExcludedFromExpenses: Boolean = false,
    val notes: String? = null,
    val transactionType: String = "DEBIT",
    val merchantRaw: String? = null,
    val merchantNormalized: String? = null,
    val providerName: String = "Bank",
    val accountLast4: String? = null,
    val upiId: String? = null,
    val referenceNumber: String? = null,
    val rawNotification: String = ""
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
            merchant = merchantNormalized ?: merchant,
            isRecurring = isRecurring,
            confidenceScore = confidenceScore,
            userCorrected = userCorrected,
            isExcludedFromExpenses = isExcludedFromExpenses,
            notes = notes,
            transactionType = com.finly.core.domain.model.TransactionType.fromString(transactionType),
            merchantRaw = merchantRaw,
            merchantNormalized = merchantNormalized,
            providerName = providerName,
            accountLast4 = accountLast4,
            upiId = upiId,
            referenceNumber = referenceNumber,
            rawNotification = rawNotification
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
                merchant = domain.merchantNormalized ?: domain.merchant,
                isRecurring = domain.isRecurring,
                confidenceScore = domain.confidenceScore,
                userCorrected = domain.userCorrected,
                isExcludedFromExpenses = domain.isExcludedFromExpenses,
                notes = domain.notes,
                transactionType = domain.transactionType.name,
                merchantRaw = domain.merchantRaw,
                merchantNormalized = domain.merchantNormalized,
                providerName = domain.providerName,
                accountLast4 = domain.accountLast4,
                upiId = domain.upiId,
                referenceNumber = domain.referenceNumber,
                rawNotification = domain.rawNotification
            )
        }
    }
}
