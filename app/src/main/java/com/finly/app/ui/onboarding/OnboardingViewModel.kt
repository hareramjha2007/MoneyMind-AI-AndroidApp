package com.finly.app.ui.onboarding

import androidx.lifecycle.ViewModel
import com.finly.core.domain.model.UserFinancialProfile
import com.finly.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    fun isOnboardingCompleted(): Boolean {
        return userPreferencesRepository.isOnboardingCompleted()
    }

    fun completeOnboarding(profile: UserFinancialProfile) {
        userPreferencesRepository.saveUserFinancialProfile(profile)
        userPreferencesRepository.setOnboardingCompleted(true)
    }

    fun skipOnboarding() {
        userPreferencesRepository.setOnboardingCompleted(true)
    }
}
