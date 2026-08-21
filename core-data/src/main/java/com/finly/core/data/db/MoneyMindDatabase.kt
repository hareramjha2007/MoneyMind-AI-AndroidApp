package com.finly.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finly.core.data.dao.CategoryDao
import com.finly.core.data.dao.CoachMessageDao
import com.finly.core.data.dao.FinancialHealthScoreDao
import com.finly.core.data.dao.GoalDao
import com.finly.core.data.dao.RecurringPaymentDao
import com.finly.core.data.dao.TransactionDao
import com.finly.core.data.entity.CategoryEntity
import com.finly.core.data.entity.CoachMessageEntity
import com.finly.core.data.entity.FinancialHealthScoreEntity
import com.finly.core.data.entity.GoalEntity
import com.finly.core.data.entity.RecurringPaymentEntity
import com.finly.core.data.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        GoalEntity::class,
        FinancialHealthScoreEntity::class,
        RecurringPaymentEntity::class,
        CoachMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MoneyMindDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun healthScoreDao(): FinancialHealthScoreDao
    abstract fun recurringPaymentDao(): RecurringPaymentDao
    abstract fun coachMessageDao(): CoachMessageDao
}
