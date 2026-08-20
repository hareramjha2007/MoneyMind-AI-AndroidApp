package com.finly.core.data.repository

import com.finly.core.data.dao.RecurringPaymentDao
import com.finly.core.data.entity.RecurringPaymentEntity
import com.finly.core.domain.model.RecurringPayment
import com.finly.core.domain.repository.RecurringPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecurringPaymentRepositoryImpl @Inject constructor(
    private val dao: RecurringPaymentDao
) : RecurringPaymentRepository {

    override fun getAllRecurringPayments(): Flow<List<RecurringPayment>> {
        return dao.getAllRecurringPayments().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getUnwantedRecurringPayments(): Flow<List<RecurringPayment>> {
        return dao.getUnwantedRecurringPayments().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertRecurringPayment(payment: RecurringPayment) {
        dao.insertRecurringPayment(RecurringPaymentEntity.fromDomain(payment))
    }

    override suspend fun markUnwanted(id: String, isUnwanted: Boolean) {
        dao.markUnwanted(id, isUnwanted)
    }
}
