package com.finly.core.domain.analytics

import com.finly.core.domain.model.FinancialHealthScore
import java.time.LocalDate

data class UserFinancialMetrics(
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val monthlySavings: Double,
    val emergencyFundBalance: Double,
    val monthlyDebtEmi: Double,
    val unwantedSubscriptionSpend: Double,
    val monthlySpendHistory: List<Double> // Past 6 months
)

class FinancialHealthScoreCalculator {

    fun calculateScore(metrics: UserFinancialMetrics, date: LocalDate = LocalDate.now()): FinancialHealthScore {
        val income = metrics.monthlyIncome.coerceAtLeast(1.0)

        // 1. Savings Ratio Score (25% weight)
        // Target: 20%+ of income saved = 100 points
        val savingsRate = (metrics.monthlySavings / income).coerceIn(0.0, 1.0)
        val savingsRatioScore = (savingsRate / 0.20 * 100).coerceIn(0.0, 100.0).toInt()

        // 2. Spending Consistency Score (20% weight)
        // Based on coefficient of variation (stdDev / mean) across 6 months
        val spendingConsistencyScore = calculateConsistencyScore(metrics.monthlySpendHistory)

        // 3. Emergency Fund Score (20% weight)
        // Target: 6 months of expenses saved = 100 points, 3 months = 50 points
        val monthlyExpenses = metrics.monthlyExpenses.coerceAtLeast(1.0)
        val emergencyMonths = metrics.emergencyFundBalance / monthlyExpenses
        val emergencyFundScore = (emergencyMonths / 6.0 * 100).coerceIn(0.0, 100.0).toInt()

        // 4. Debt Ratio Score (20% weight)
        // Target: 0% EMI/debt = 100 points, >50% debt = 0 points
        val debtRatio = (metrics.monthlyDebtEmi / income).coerceIn(0.0, 1.0)
        val debtRatioScore = ((1.0 - (debtRatio / 0.50)) * 100).coerceIn(0.0, 100.0).toInt()

        // 5. Subscription Waste Score (15% weight)
        // Target: 0 waste = 100 points, 5% of income wasted = 0 points
        val wasteRatio = (metrics.unwantedSubscriptionSpend / income).coerceIn(0.0, 1.0)
        val subscriptionWasteScore = ((1.0 - (wasteRatio / 0.05)) * 100).coerceIn(0.0, 100.0).toInt()

        // Weighted total sum
        val totalScore = (
            0.25 * savingsRatioScore +
            0.20 * spendingConsistencyScore +
            0.20 * emergencyFundScore +
            0.20 * debtRatioScore +
            0.15 * subscriptionWasteScore
        ).toInt().coerceIn(0, 100)

        return FinancialHealthScore(
            date = date.toString(),
            totalScore = totalScore,
            savingsRatioScore = savingsRatioScore,
            spendingConsistencyScore = spendingConsistencyScore,
            emergencyFundScore = emergencyFundScore,
            debtRatioScore = debtRatioScore,
            subscriptionWasteScore = subscriptionWasteScore
        )
    }

    private fun calculateConsistencyScore(spendHistory: List<Double>): Int {
        if (spendHistory.size < 2) return 75 // Neutral default for cold start

        val mean = spendHistory.average()
        if (mean == 0.0) return 100

        val variance = spendHistory.sumOf { Math.pow(it - mean, 2.0) } / spendHistory.size
        val stdDev = Math.sqrt(variance)
        val cv = stdDev / mean

        // If Coefficient of Variation is 0 (identical spend), score = 100. If CV >= 0.5 (high fluctuation), score -> 0
        val score = ((1.0 - (cv / 0.50)) * 100).coerceIn(0.0, 100.0)
        return score.toInt()
    }
}
