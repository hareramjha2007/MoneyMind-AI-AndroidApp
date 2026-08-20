package com.finly.core.domain.model

data class FinancialHealthScore(
    val date: String, // YYYY-MM-DD
    val totalScore: Int, // 0-100
    val savingsRatioScore: Int, // 0-100 (25% weight)
    val spendingConsistencyScore: Int, // 0-100 (20% weight)
    val emergencyFundScore: Int, // 0-100 (20% weight)
    val debtRatioScore: Int, // 0-100 (20% weight)
    val subscriptionWasteScore: Int // 0-100 (15% weight)
)
