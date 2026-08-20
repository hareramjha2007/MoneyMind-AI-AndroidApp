package com.finly.core.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FinancialHealthScoreCalculatorTest {

    private lateinit var calculator: FinancialHealthScoreCalculator

    @Before
    fun setUp() {
        calculator = FinancialHealthScoreCalculator()
    }

    @Test
    fun `calculateScore returns high score for healthy finances`() {
        val metrics = UserFinancialMetrics(
            monthlyIncome = 100000.0,
            monthlyExpenses = 50000.0,
            monthlySavings = 30000.0, // 30% savings rate (100 pts)
            emergencyFundBalance = 300000.0, // 6 months expenses (100 pts)
            monthlyDebtEmi = 0.0, // 0% debt (100 pts)
            unwantedSubscriptionSpend = 0.0, // 0 waste (100 pts)
            monthlySpendHistory = listOf(50000.0, 51000.0, 49000.0, 50000.0, 52000.0, 48000.0) // high consistency
        )

        val score = calculator.calculateScore(metrics, date = LocalDate.of(2026, 8, 15))

        assertTrue(score.totalScore >= 85)
        assertEquals(100, score.savingsRatioScore)
        assertEquals(100, score.emergencyFundScore)
        assertEquals(100, score.debtRatioScore)
        assertEquals(100, score.subscriptionWasteScore)
    }

    @Test
    fun `calculateScore penalizes high debt and zero emergency fund`() {
        val metrics = UserFinancialMetrics(
            monthlyIncome = 80000.0,
            monthlyExpenses = 75000.0,
            monthlySavings = 0.0, // 0% savings rate (0 pts)
            emergencyFundBalance = 0.0, // 0 emergency fund (0 pts)
            monthlyDebtEmi = 40000.0, // 50% debt ratio (0 pts)
            unwantedSubscriptionSpend = 4000.0, // 5% waste (0 pts)
            monthlySpendHistory = listOf(30000.0, 75000.0, 20000.0, 85000.0)
        )

        val score = calculator.calculateScore(metrics, date = LocalDate.of(2026, 8, 15))

        assertTrue(score.totalScore < 40)
        assertEquals(0, score.savingsRatioScore)
        assertEquals(0, score.emergencyFundScore)
        assertEquals(0, score.debtRatioScore)
        assertEquals(0, score.subscriptionWasteScore)
    }
}
