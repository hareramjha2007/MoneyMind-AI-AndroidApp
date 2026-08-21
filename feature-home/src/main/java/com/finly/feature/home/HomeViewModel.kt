package com.finly.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finly.core.domain.ai.CoachProvider
import com.finly.core.domain.ai.CoachRequest
import com.finly.core.domain.analytics.FinancialHealthScoreCalculator
import com.finly.core.domain.analytics.UserFinancialMetrics
import com.finly.core.domain.model.CategoryChange
import com.finly.core.domain.model.FinancialHealthScore
import com.finly.core.domain.model.FinancialSummary
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalSummary
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.repository.GoalRepository
import com.finly.core.domain.repository.HealthScoreRepository
import com.finly.core.domain.repository.TransactionRepository
import com.finly.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val score: FinancialHealthScore? = null,
    val goals: List<Goal> = emptyList(),
    val behavioralHighlights: List<String> = emptyList(),
    val aiInsightText: String = "",
    val daysOfData: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthScoreRepository: HealthScoreRepository,
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository,
    private val coachProvider: CoachProvider,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val scoreCalculator = FinancialHealthScoreCalculator()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                transactionRepository.getAllTransactions(),
                goalRepository.getAllGoals()
            ) { transactions, goals ->
                Pair(transactions, goals)
            }.collect { (transactions, goals) ->
                val userProfile = userPreferencesRepository.getUserFinancialProfile()

                // Calculate dynamic metrics from Room DB or baseline setup profile
                val income = transactions.filter { it.amount > 0 && it.direction == TransactionDirection.CREDIT }
                    .sumOf { it.amount }
                
                val expenses = transactions.filter { it.amount > 0 && it.direction == TransactionDirection.DEBIT }
                    .sumOf { it.amount }

                val effectiveIncome = if (income > 0) income else userProfile.monthlyIncome.coerceAtLeast(50000.0)
                val effectiveExpenses = if (expenses > 0) expenses else (effectiveIncome * 0.29)
                val effectiveEmergency = if (goals.isNotEmpty()) goals.first().currentAmount else userProfile.emergencyFundAmount.coerceAtLeast(100000.0)

                val netSavings = (effectiveIncome - effectiveExpenses).coerceAtLeast(0.0)
                val savingsRatePct = ((netSavings / effectiveIncome) * 100.0).coerceIn(0.0, 100.0)

                val catGroup = transactions.filter { it.direction == TransactionDirection.DEBIT }
                    .groupBy { it.categoryId.lowercase() }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val recurringTx = transactions.filter {
                    it.categoryId.contains("subscription", ignoreCase = true) ||
                    it.merchant?.let { m -> listOf("netflix", "spotify", "gym", "amazon", "cloud", "swiggy", "zomato").any { kw -> m.lowercase().contains(kw) } } == true
                }
                val recurringCount = recurringTx.size
                val recurringSum = recurringTx.sumOf { it.amount }.toInt()

                val cal = Calendar.getInstance()
                val weekendDebit = transactions.filter {
                    it.direction == TransactionDirection.DEBIT && run {
                        cal.timeInMillis = it.timestamp
                        val day = cal.get(Calendar.DAY_OF_WEEK)
                        day == Calendar.SATURDAY || day == Calendar.SUNDAY
                    }
                }.sumOf { it.amount }

                val weekendPct = if (expenses > 0) ((weekendDebit / expenses) * 100).toInt() else 0
                val primaryGoal = goals.firstOrNull()

                // Always calculate a baseline/live Financial Health Score
                val userMetrics = UserFinancialMetrics(
                    monthlyIncome = effectiveIncome,
                    monthlyExpenses = effectiveExpenses,
                    monthlySavings = netSavings,
                    emergencyFundBalance = effectiveEmergency,
                    monthlyDebtEmi = if (transactions.isNotEmpty()) transactions.filter { it.categoryId.contains("debt", ignoreCase = true) || it.categoryId.contains("emi", ignoreCase = true) }.sumOf { it.amount } else userProfile.monthlyEmi,
                    unwantedSubscriptionSpend = recurringSum.toDouble(),
                    monthlySpendHistory = listOf(effectiveExpenses * 0.95, effectiveExpenses)
                )

                val computedScore = scoreCalculator.calculateScore(userMetrics)

                val dynamicBehavioral = if (transactions.isNotEmpty()) {
                    val p1 = if (weekendDebit > 0) "Weekend spend: ${weekendPct}% of monthly expenses (₹${weekendDebit.toInt()})" else "Weekend spending: ₹0 logged so far"
                    val p2 = if (recurringCount > 0) "$recurringCount recurring charges detected totaling ₹$recurringSum" else "No recurring subscription charges detected yet"
                    val topCatName = catGroup.maxByOrNull { it.value }?.key?.replaceFirstChar { it.uppercase() } ?: "None"
                    val topCatAmt = catGroup.maxByOrNull { it.value }?.value?.toInt() ?: 0
                    val p3 = if (topCatAmt > 0) "Top spending category: $topCatName at ₹$topCatAmt" else "Top category: Pending transaction data"
                    listOf(p1, p2, p3)
                } else {
                    listOf(
                        "No bank notifications parsed yet",
                        "CapitalCurb AI will automatically track expenses as notifications arrive",
                        "Tap top-left Profile icon (👤) or Goals tab to manage your baseline targets"
                    )
                }

                val dynamicAiStrategy = if (transactions.isNotEmpty()) {
                    if (primaryGoal != null) "Savings rate is steady at ${Math.round(savingsRatePct)}%. Track your ${primaryGoal.title} goal progress in the Goals tab."
                    else "Savings rate: ${Math.round(savingsRatePct)}%. Tap Goals tab to set your first savings target."
                } else {
                    "Welcome to CapitalCurb AI! Based on your setup profile, your baseline health score is ${computedScore.totalScore}/100. Enable notification listener to auto-track expenses."
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    score = computedScore,
                    goals = goals,
                    behavioralHighlights = dynamicBehavioral,
                    aiInsightText = dynamicAiStrategy,
                    daysOfData = transactions.size
                )
            }
        }
    }
}
