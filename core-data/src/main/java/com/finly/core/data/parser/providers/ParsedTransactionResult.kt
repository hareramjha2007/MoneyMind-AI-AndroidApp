package com.finly.core.data.parser.providers

import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.model.TransactionType

data class ParsedTransactionResult(
    val amount: Double,
    val direction: TransactionDirection,
    val transactionType: TransactionType,
    val merchantRaw: String?,
    val merchantNormalized: String?,
    val category: String,
    val providerName: String,
    val sourceApp: String,
    val accountLast4: String?,
    val upiId: String?,
    val referenceNumber: String?,
    val timestamp: Long,
    val confidenceScore: Float,
    val rawNotification: String
)
