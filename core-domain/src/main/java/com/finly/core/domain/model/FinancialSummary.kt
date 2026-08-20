package com.finly.core.domain.model

data class CategoryChange(
    val category: String,
    val changePct: Double,
    val amount: Double
)

data class GoalSummary(
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val progressPct: Float,
    val onTrack: Boolean = true
)

data class FinancialSummary(
    val period: String, // e.g. "2026-08"
    val income: Double,
    val savingsRatePct: Double,
    val savingsRateChangePct: Double,
    val categoryChanges: List<CategoryChange>,
    val goals: List<GoalSummary>,
    val flags: List<String>,
    val healthInsuranceCover: Double = 0.0,
    val termInsuranceCover: Double = 0.0,
    val emergencyFundSaved: Double = 0.0
)
