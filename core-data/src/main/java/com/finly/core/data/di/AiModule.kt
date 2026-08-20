package com.finly.core.data.di

import com.finly.core.data.ai.GeminiCoachProviderImpl
import com.finly.core.domain.ai.CoachProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindCoachProvider(
        impl: GeminiCoachProviderImpl
    ): CoachProvider
}
