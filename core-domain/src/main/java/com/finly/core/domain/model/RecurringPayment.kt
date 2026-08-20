package com.finly.core.domain.model

enum class PaymentCadence {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    ANNUALLY
}

data class RecurringPayment(
    val id: String,
    val merchant: String,
    val amount: Double,
    val cadence: PaymentCadence,
    val lastChargedDate: Long,
    val nextExpectedDate: Long,
    val categoryId: String,
    val userMarkedUnwanted: Boolean = false
)
