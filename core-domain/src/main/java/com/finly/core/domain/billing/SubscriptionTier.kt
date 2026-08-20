package com.finly.core.domain.billing

enum class SubscriptionPlan(
    val id: String,
    val title: String,
    val billingPeriod: String,
    val totalPrice: Int,
    val monthlyEquivalent: Int,
    val discountPercent: Int,
    val isBestValue: Boolean = false
) {
    FREE_TRIAL(
        id = "plan_free_trial",
        title = "14-Day Free Trial",
        billingPeriod = "Full Access Included for 14 Days",
        totalPrice = 0,
        monthlyEquivalent = 0,
        discountPercent = 100
    ),
    MONTHLY(
        id = "plan_monthly",
        title = "1 Month",
        billingPeriod = "Billed monthly",
        totalPrice = 99,
        monthlyEquivalent = 99,
        discountPercent = 0
    ),
    QUARTERLY(
        id = "plan_quarterly",
        title = "3 Months",
        billingPeriod = "Billed ₹249 every 3 months",
        totalPrice = 249,
        monthlyEquivalent = 83,
        discountPercent = 16
    ),
    HALF_YEARLY(
        id = "plan_half_yearly",
        title = "6 Months",
        billingPeriod = "Billed ₹399 every 6 months",
        totalPrice = 399,
        monthlyEquivalent = 66,
        discountPercent = 33
    ),
    ANNUAL(
        id = "plan_annual",
        title = "1 Year",
        billingPeriod = "Billed ₹599 annually",
        totalPrice = 599,
        monthlyEquivalent = 50,
        discountPercent = 50,
        isBestValue = true
    )
}

enum class SubscriptionTier(
    val title: String,
    val maxGoalsAllowed: Int,
    val allowsAiCoach: Boolean,
    val allowsGoalForecasting: Boolean,
    val allowsFamilyMode: Boolean
) {
    FREE(
        title = "Free",
        maxGoalsAllowed = 1,
        allowsAiCoach = false,
        allowsGoalForecasting = false,
        allowsFamilyMode = false
    ),
    PREMIUM(
        title = "Premium",
        maxGoalsAllowed = Int.MAX_VALUE,
        allowsAiCoach = true,
        allowsGoalForecasting = true,
        allowsFamilyMode = true
    )
}
