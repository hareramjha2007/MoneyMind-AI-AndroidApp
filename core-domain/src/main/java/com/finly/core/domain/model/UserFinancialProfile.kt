package com.finly.core.domain.model

data class UserFinancialProfile(
    val monthlyIncome: Double = 85000.0,
    val emergencyFundAmount: Double = 0.0,
    val hasHealthInsurance: Boolean = false,
    val healthInsuranceCover: Double = 0.0,
    val hasTermInsurance: Boolean = false,
    val termInsuranceCover: Double = 0.0,
    val monthlyEmi: Double = 0.0
)
