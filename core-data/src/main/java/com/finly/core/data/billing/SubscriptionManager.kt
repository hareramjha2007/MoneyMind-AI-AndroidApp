package com.finly.core.data.billing

import android.content.Context
import com.finly.core.domain.billing.SubscriptionPlan
import com.finly.core.domain.billing.SubscriptionTier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _currentPlan = MutableStateFlow<SubscriptionPlan?>(SubscriptionPlan.ANNUAL)
    val currentPlan: StateFlow<SubscriptionPlan?> = _currentPlan.asStateFlow()

    private val _currentTier = MutableStateFlow(SubscriptionTier.PREMIUM)
    val currentTier: StateFlow<SubscriptionTier> = _currentTier.asStateFlow()

    fun canAccessAiCoach(): Boolean = _currentTier.value.allowsAiCoach
    fun canAccessGoalForecasting(): Boolean = _currentTier.value.allowsGoalForecasting

    fun selectPlan(plan: SubscriptionPlan) {
        _currentPlan.value = plan
        _currentTier.value = SubscriptionTier.PREMIUM
    }
}
