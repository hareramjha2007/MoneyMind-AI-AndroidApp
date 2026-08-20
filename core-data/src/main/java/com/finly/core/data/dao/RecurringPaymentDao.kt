package com.finly.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finly.core.data.entity.RecurringPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringPaymentDao {
    @Query("SELECT * FROM recurring_payments ORDER BY nextExpectedDate ASC")
    fun getAllRecurringPayments(): Flow<List<RecurringPaymentEntity>>

    @Query("SELECT * FROM recurring_payments WHERE userMarkedUnwanted = 1 ORDER BY nextExpectedDate ASC")
    fun getUnwantedRecurringPayments(): Flow<List<RecurringPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringPayment(payment: RecurringPaymentEntity)

    @Query("UPDATE recurring_payments SET userMarkedUnwanted = :isUnwanted WHERE id = :id")
    suspend fun markUnwanted(id: String, isUnwanted: Boolean)
}
