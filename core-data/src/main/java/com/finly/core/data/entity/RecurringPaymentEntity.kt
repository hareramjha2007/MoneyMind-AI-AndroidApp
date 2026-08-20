package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.PaymentCadence
import com.finly.core.domain.model.RecurringPayment

@Entity(tableName = "recurring_payments")
data class RecurringPaymentEntity(
    @PrimaryKey val id: String,
    val merchant: String,
    val amount: Double,
    val cadence: String, // WEEKLY, MONTHLY, QUARTERLY, ANNUALLY
    val lastChargedDate: Long,
    val nextExpectedDate: Long,
    val categoryId: String,
    val userMarkedUnwanted: Boolean
) {
    fun toDomain(): RecurringPayment {
        return RecurringPayment(
            id = id,
            merchant = merchant,
            amount = amount,
            cadence = PaymentCadence.valueOf(cadence),
            lastChargedDate = lastChargedDate,
            nextExpectedDate = nextExpectedDate,
            categoryId = categoryId,
            userMarkedUnwanted = userMarkedUnwanted
        )
    }

    companion object {
        fun fromDomain(domain: RecurringPayment): RecurringPaymentEntity {
            return RecurringPaymentEntity(
                id = domain.id,
                merchant = domain.merchant,
                amount = domain.amount,
                cadence = domain.cadence.name,
                lastChargedDate = domain.lastChargedDate,
                nextExpectedDate = domain.nextExpectedDate,
                categoryId = domain.categoryId,
                userMarkedUnwanted = domain.userMarkedUnwanted
            )
        }
    }
}
