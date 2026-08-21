package com.finly.core.domain.model

enum class TransactionDirection {
    DEBIT,
    CREDIT
}

data class Transaction(
    val id: String,
    val amount: Double,
    val direction: TransactionDirection,
    val timestamp: Long,
    val sourceApp: String,
    val rawSenderId: String,
    val categoryId: String,
    val merchant: String?,
    val isRecurring: Boolean = false,
    val confidenceScore: Float = 1.0f,
    val userCorrected: Boolean = false,
    val isExcludedFromExpenses: Boolean = false,
    val notes: String? = null,
    // Expanded Normalized Signal Fields
    val transactionType: TransactionType = TransactionType.DEBIT,
    val merchantRaw: String? = null,
    val merchantNormalized: String? = null,
    val providerName: String = "Bank",
    val accountLast4: String? = null,
    val upiId: String? = null,
    val referenceNumber: String? = null,
    val rawNotification: String = ""
)
