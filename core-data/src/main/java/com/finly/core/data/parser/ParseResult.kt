package com.finly.core.data.parser

import com.finly.core.domain.model.TransactionDirection

data class ParsedTransaction(
    val amount: Double,
    val direction: TransactionDirection,
    val merchant: String?,
    val categoryId: String,
    val rawSenderId: String,
    val sourceApp: String,
    val confidenceScore: Float
)

sealed class ParseResult {
    data class Success(val transaction: ParsedTransaction) : ParseResult()
    object IgnoredNonTransactional : ParseResult() // OTP, Promo, balance enquiry
    object FailedToParse : ParseResult()
}
