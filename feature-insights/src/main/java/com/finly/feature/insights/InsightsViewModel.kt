package com.finly.feature.insights

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finly.core.domain.analytics.FinancialHealthScoreCalculator
import com.finly.core.domain.analytics.UserFinancialMetrics
import com.finly.core.domain.model.FinancialHealthScore
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalType
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.repository.GoalRepository
import com.finly.core.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryBreakdownItem(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val colorHex: Long
)

data class SubscriptionItem(
    val id: String,
    val name: String,
    val amount: Double,
    val cadence: String,
    val isUnwanted: Boolean = true
)

data class InsightsUiState(
    val isLoading: Boolean = true,
    val score: FinancialHealthScore? = null,
    val totalExpenses: Double = 0.0,
    val totalIncome: Double = 0.0,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val subscriptions: List<SubscriptionItem> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isDrillDownOpen: Boolean = false,
    val selectedCategoryFilter: String? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val scoreCalculator = FinancialHealthScoreCalculator()

    init {
        loadInsights()
    }

    fun toggleDrillDownSheet(open: Boolean, categoryFilter: String? = null) {
        _uiState.value = _uiState.value.copy(
            isDrillDownOpen = open,
            selectedCategoryFilter = categoryFilter
        )
    }

    fun updateTransactionDetails(id: String, categoryId: String, isExcludedFromExpenses: Boolean, notes: String?) {
        viewModelScope.launch {
            transactionRepository.updateTransactionDetails(id, categoryId, isExcludedFromExpenses, notes)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(id)
        }
    }

    fun toggleSubscriptionUnwanted(subId: String) {
        val updated = _uiState.value.subscriptions.map { sub ->
            if (sub.id == subId) sub.copy(isUnwanted = !sub.isUnwanted) else sub
        }
        val unwantedSum = updated.filter { it.isUnwanted }.sumOf { it.amount }
        
        val currentScoreObj = _uiState.value.score
        if (currentScoreObj != null && (_uiState.value.totalIncome > 0 || _uiState.value.totalExpenses > 0)) {
            val userMetrics = UserFinancialMetrics(
                monthlyIncome = _uiState.value.totalIncome,
                monthlyExpenses = _uiState.value.totalExpenses,
                monthlySavings = (_uiState.value.totalIncome - _uiState.value.totalExpenses).coerceAtLeast(0.0),
                emergencyFundBalance = 0.0,
                monthlyDebtEmi = 0.0,
                unwantedSubscriptionSpend = unwantedSum,
                monthlySpendHistory = listOf(_uiState.value.totalExpenses * 0.95, _uiState.value.totalExpenses)
            )
            val newScore = scoreCalculator.calculateScore(userMetrics)
            _uiState.value = _uiState.value.copy(subscriptions = updated, score = newScore)
        } else {
            _uiState.value = _uiState.value.copy(subscriptions = updated)
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            combine(
                transactionRepository.getAllTransactions(),
                goalRepository.getAllGoals()
            ) { transactions, goals ->
                Pair(transactions, goals)
            }.collect { (transactions, goals) ->
                if (transactions.isEmpty()) {
                    _uiState.value = InsightsUiState(
                        isLoading = false,
                        score = null,
                        totalExpenses = 0.0,
                        totalIncome = 0.0,
                        categoryBreakdown = emptyList(),
                        subscriptions = emptyList(),
                        transactions = emptyList()
                    )
                    return@collect
                }

                val income = transactions.filter { it.amount > 0 && it.direction == TransactionDirection.CREDIT }
                    .sumOf { it.amount }
                
                val expenses = transactions.filter { it.amount > 0 && it.direction == TransactionDirection.DEBIT && !it.isExcludedFromExpenses }
                    .sumOf { it.amount }

                val netSavings = (income - expenses).coerceAtLeast(0.0)

                // Category Expense Breakdown
                val categoryMap = transactions.filter { it.direction == TransactionDirection.DEBIT && !it.isExcludedFromExpenses }
                    .groupBy { it.categoryId.ifBlank { "Other" } }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val colors = listOf(0xFF6366F1, 0xFFA855F7, 0xFF06B6D4, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444)
                var colorIdx = 0

                val breakdownItems = categoryMap.map { (catName, amt) ->
                    val pct = if (expenses > 0) ((amt / expenses) * 100.0).toFloat() else 0f
                    val colorHex = colors[colorIdx % colors.size]
                    colorIdx++
                    CategoryBreakdownItem(
                        category = catName.replaceFirstChar { it.uppercase() },
                        amount = amt,
                        percentage = Math.round(pct * 10.0f) / 10.0f,
                        colorHex = colorHex
                    )
                }.sortedByDescending { it.amount }

                // Detected Subscriptions from DB
                val subTransactions = transactions.filter {
                    it.direction == TransactionDirection.DEBIT && (
                        it.isRecurring ||
                        it.categoryId.contains("subscription", ignoreCase = true) ||
                        it.merchant?.let { m -> listOf("netflix", "spotify", "gym", "cloud", "amazon", "apple").any { kw -> m.lowercase().contains(kw) } } == true
                    )
                }

                val detectedSubs = subTransactions.mapIndexed { idx, tx ->
                    SubscriptionItem(
                        id = tx.id,
                        name = tx.merchant ?: tx.categoryId,
                        amount = tx.amount,
                        cadence = "Monthly",
                        isUnwanted = true
                    )
                }

                val emergencyFundGoal = goals.firstOrNull { it.type == GoalType.EMERGENCY_FUND }
                val emBalance = emergencyFundGoal?.currentAmount ?: 0.0

                val unwantedWasteSum = detectedSubs.filter { it.isUnwanted }.sumOf { it.amount }

                // Calculate Health Score from real database metrics
                val computedScore = if (income > 0 || expenses > 0) {
                    val userMetrics = UserFinancialMetrics(
                        monthlyIncome = income,
                        monthlyExpenses = expenses,
                        monthlySavings = netSavings,
                        emergencyFundBalance = emBalance,
                        monthlyDebtEmi = transactions.filter { it.categoryId.contains("debt", ignoreCase = true) || it.categoryId.contains("emi", ignoreCase = true) }.sumOf { it.amount },
                        unwantedSubscriptionSpend = unwantedWasteSum,
                        monthlySpendHistory = listOf(expenses * 0.95, expenses)
                    )
                    scoreCalculator.calculateScore(userMetrics)
                } else null

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    score = computedScore,
                    totalExpenses = expenses,
                    totalIncome = income,
                    categoryBreakdown = breakdownItems,
                    subscriptions = detectedSubs,
                    transactions = transactions
                )
            }
        }
    }
}
