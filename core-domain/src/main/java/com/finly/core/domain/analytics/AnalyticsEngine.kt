package com.finly.core.domain.analytics

import com.finly.core.domain.model.CategoryChange
import com.finly.core.domain.model.FinancialSummary
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalSummary
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.model.TransactionDirection

class AnalyticsEngine {

    fun generateAnonymizedSummary(
        period: String,
        income: Double,
        currentMonthTransactions: List<Transaction>,
        previousMonthTransactions: List<Transaction>,
        goals: List<Goal>,
        unwantedSubscriptionsCount: Int
    ): FinancialSummary {
        val currentExpenses = currentMonthTransactions
            .filter { it.direction == TransactionDirection.DEBIT }
            .sumOf { it.amount }

        val savings = (income - currentExpenses).coerceAtLeast(0.0)
        val savingsRatePct = if (income > 0) (savings / income) * 100 else 0.0

        val prevExpenses = previousMonthTransactions
            .filter { it.direction == TransactionDirection.DEBIT }
            .sumOf { it.amount }
        val prevSavings = (income - prevExpenses).coerceAtLeast(0.0)
        val prevSavingsRatePct = if (income > 0) (prevSavings / income) * 100 else 0.0

        val savingsRateChangePct = savingsRatePct - prevSavingsRatePct

        // Category breakdown changes
        val currentByCategory = currentMonthTransactions
            .filter { it.direction == TransactionDirection.DEBIT }
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val prevByCategory = previousMonthTransactions
            .filter { it.direction == TransactionDirection.DEBIT }
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val categoryChanges = currentByCategory.map { (catId, currentAmt) ->
            val prevAmt = prevByCategory[catId] ?: 0.0
            val changePct = if (prevAmt > 0) ((currentAmt - prevAmt) / prevAmt) * 100 else 100.0
            CategoryChange(
                category = catId,
                changePct = Math.round(changePct * 10.0) / 10.0,
                amount = currentAmt
            )
        }

        val goalSummaries = goals.map { goal ->
            GoalSummary(
                name = goal.title,
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                progressPct = goal.progressPercentage,
                onTrack = goal.aiProjectedCompletionDate == null || goal.aiProjectedCompletionDate <= goal.targetDate
            )
        }

        val flags = mutableListOf<String>()
        if (unwantedSubscriptionsCount > 0) {
            flags.add("subscription_unused_${unwantedSubscriptionsCount}_detected")
        }
        if (savingsRateChangePct < -5) {
            flags.add("savings_rate_dropped")
        }

        return FinancialSummary(
            period = period,
            income = income,
            savingsRatePct = Math.round(savingsRatePct * 10.0) / 10.0,
            savingsRateChangePct = Math.round(savingsRateChangePct * 10.0) / 10.0,
            categoryChanges = categoryChanges,
            goals = goalSummaries,
            flags = flags
        )
    }
}
