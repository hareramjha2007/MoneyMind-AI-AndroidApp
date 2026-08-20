package com.finly.core.data.di

import com.finly.core.data.repository.CategoryRepositoryImpl
import com.finly.core.data.repository.CoachRepositoryImpl
import com.finly.core.data.repository.GoalRepositoryImpl
import com.finly.core.data.repository.HealthScoreRepositoryImpl
import com.finly.core.data.repository.RecurringPaymentRepositoryImpl
import com.finly.core.data.repository.TransactionRepositoryImpl
import com.finly.core.data.repository.UserPreferencesRepositoryImpl
import com.finly.core.domain.repository.CategoryRepository
import com.finly.core.domain.repository.CoachRepository
import com.finly.core.domain.repository.GoalRepository
import com.finly.core.domain.repository.HealthScoreRepository
import com.finly.core.domain.repository.RecurringPaymentRepository
import com.finly.core.domain.repository.TransactionRepository
import com.finly.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        impl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindHealthScoreRepository(
        impl: HealthScoreRepositoryImpl
    ): HealthScoreRepository

    @Binds
    @Singleton
    abstract fun bindRecurringPaymentRepository(
        impl: RecurringPaymentRepositoryImpl
    ): RecurringPaymentRepository

    @Binds
    @Singleton
    abstract fun bindCoachRepository(
        impl: CoachRepositoryImpl
    ): CoachRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
