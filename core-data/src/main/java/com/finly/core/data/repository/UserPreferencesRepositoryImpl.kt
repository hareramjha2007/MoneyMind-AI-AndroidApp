package com.finly.core.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.finly.core.domain.model.UserFinancialProfile
import com.finly.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("moneymind_user_prefs", Context.MODE_PRIVATE)

    override fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("is_onboarding_completed", false)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("is_onboarding_completed", completed).apply()
    }

    override fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("is_biometric_enabled", false)
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_biometric_enabled", enabled).apply()
    }

    override fun saveUserFinancialProfile(profile: UserFinancialProfile) {
        prefs.edit()
            .putFloat("monthly_income", profile.monthlyIncome.toFloat())
            .putFloat("emergency_fund", profile.emergencyFundAmount.toFloat())
            .putBoolean("has_health_ins", profile.hasHealthInsurance)
            .putFloat("health_ins_cover", profile.healthInsuranceCover.toFloat())
            .putBoolean("has_term_ins", profile.hasTermInsurance)
            .putFloat("term_ins_cover", profile.termInsuranceCover.toFloat())
            .putFloat("monthly_emi", profile.monthlyEmi.toFloat())
            .apply()
    }

    override fun getUserFinancialProfile(): UserFinancialProfile {
        return UserFinancialProfile(
            monthlyIncome = prefs.getFloat("monthly_income", 85000f).toDouble(),
            emergencyFundAmount = prefs.getFloat("emergency_fund", 0f).toDouble(),
            hasHealthInsurance = prefs.getBoolean("has_health_ins", false),
            healthInsuranceCover = prefs.getFloat("health_ins_cover", 0f).toDouble(),
            hasTermInsurance = prefs.getBoolean("has_term_ins", false),
            termInsuranceCover = prefs.getFloat("term_ins_cover", 0f).toDouble(),
            monthlyEmi = prefs.getFloat("monthly_emi", 0f).toDouble()
        )
    }

    override fun getCurrencyCode(): String {
        val saved = prefs.getString("selected_currency_code", null)
        if (!saved.isNullOrBlank()) return saved

        val country = java.util.Locale.getDefault().country
        return when (country.uppercase(java.util.Locale.ROOT)) {
            "IN" -> "INR"
            "US" -> "USD"
            "GB" -> "GBP"
            "DE", "FR", "IT", "ES", "NL", "BE", "AT", "FI", "IE", "PT" -> "EUR"
            "AE" -> "AED"
            "CA" -> "CAD"
            "AU" -> "AUD"
            "SG" -> "SGD"
            else -> "INR"
        }
    }

    override fun setCurrencyCode(code: String) {
        prefs.edit().putString("selected_currency_code", code).apply()
    }
}
