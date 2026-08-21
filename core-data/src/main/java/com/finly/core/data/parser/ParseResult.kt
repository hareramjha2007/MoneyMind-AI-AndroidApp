package com.finly.core.data.parser

import com.finly.core.domain.model.TransactionDirection

data class ParsedTransaction(
    val amount: Double,
    val direction: TransactionDirection,
    val merchant: String?,
    val categoryId: String,
    val rawSenderId: String,
    val sourceApp: String,
    val confidenceScore: Float,
    val transactionType: com.finly.core.domain.model.TransactionType = com.finly.core.domain.model.TransactionType.DEBIT,
    val merchantRaw: String? = null,
    val merchantNormalized: String? = null,
    val providerName: String = "Bank",
    val accountLast4: String? = null,
    val upiId: String? = null,
    val referenceNumber: String? = null,
    val rawNotification: String = ""
)

sealed class ParseResult {
    data class Success(val transaction: ParsedTransaction) : ParseResult()
    object IgnoredNonTransactional : ParseResult() // OTP, Promo, balance enquiry
    object FailedToParse : ParseResult()
}
