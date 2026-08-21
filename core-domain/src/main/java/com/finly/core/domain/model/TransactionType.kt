package com.finly.core.domain.model

enum class TransactionType {
    DEBIT,
    CREDIT,
    REFUND,
    SALARY,
    TRANSFER,
    UPI,
    CARD,
    ATM,
    CASH_DEPOSIT;

    companion object {
        fun fromString(value: String?): TransactionType {
            if (value == null) return DEBIT
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                DEBIT
            }
        }
    }
}
