package com.finly.core.domain.repository

import com.finly.core.domain.model.RecurringPayment
import kotlinx.coroutines.flow.Flow

interface RecurringPaymentRepository {
    fun getAllRecurringPayments(): Flow<List<RecurringPayment>>
    fun getUnwantedRecurringPayments(): Flow<List<RecurringPayment>>
    suspend fun insertRecurringPayment(payment: RecurringPayment)
    suspend fun markUnwanted(id: String, isUnwanted: Boolean)
}
