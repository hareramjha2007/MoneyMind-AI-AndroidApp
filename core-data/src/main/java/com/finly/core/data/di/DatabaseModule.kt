package com.finly.core.data.di

import android.content.Context
import androidx.room.Room
import com.finly.core.data.dao.CategoryDao
import com.finly.core.data.dao.CoachMessageDao
import com.finly.core.data.dao.FinancialHealthScoreDao
import com.finly.core.data.dao.GoalDao
import com.finly.core.data.dao.RecurringPaymentDao
import com.finly.core.data.dao.TransactionDao
import com.finly.core.data.db.MoneyMindDatabase
import com.finly.core.data.security.PassphraseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePassphraseManager(
        @ApplicationContext context: Context
    ): PassphraseManager = PassphraseManager(context)

    @Provides
    @Singleton
    fun provideMoneyMindDatabase(
        @ApplicationContext context: Context,
        passphraseManager: PassphraseManager
    ): MoneyMindDatabase {
        val passphrase = passphraseManager.getOrGeneratePassphrase()
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            MoneyMindDatabase::class.java,
            "moneymind_encrypted.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionDao(db: MoneyMindDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: MoneyMindDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideGoalDao(db: MoneyMindDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideHealthScoreDao(db: MoneyMindDatabase): FinancialHealthScoreDao = db.healthScoreDao()

    @Provides
    fun provideRecurringPaymentDao(db: MoneyMindDatabase): RecurringPaymentDao = db.recurringPaymentDao()

    @Provides
    fun provideCoachMessageDao(db: MoneyMindDatabase): CoachMessageDao = db.coachMessageDao()
}
