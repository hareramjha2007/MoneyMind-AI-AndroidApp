package com.finly.core.domain.repository

import com.finly.core.domain.model.UserFinancialProfile

interface UserPreferencesRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
    fun isBiometricEnabled(): Boolean
    fun setBiometricEnabled(enabled: Boolean)
    fun saveUserFinancialProfile(profile: UserFinancialProfile)
    fun getUserFinancialProfile(): UserFinancialProfile
}
