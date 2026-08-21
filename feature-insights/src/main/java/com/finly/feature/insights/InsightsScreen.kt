package com.finly.feature.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreNeedsWork
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark
import com.finly.core.ui.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val score = state.score

    var selectedTransactionForDetail by remember { mutableStateOf<com.finly.core.domain.model.Transaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Profile Vault",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Behavioral Insights",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Expenses & Drill-Down Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "Total Monthly Expenses",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatInr(state.totalExpenses),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleDrillDownSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.ReceiptLong,
                                contentDescription = "Drill Down",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Drill Down",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category Expenses Breakdown Card & Donut Chart
        val categorySpendItems = state.categoryBreakdown.map { cat ->
            com.finly.core.ui.components.CategorySpendItem(
                category = com.finly.core.ui.model.TransactionCategory.fromId(cat.category),
                totalSpend = cat.amount,
                count = state.transactions.count { it.direction == TransactionDirection.DEBIT && !it.isExcludedFromExpenses && com.finly.core.ui.model.TransactionCategory.fromId(it.categoryId).displayName == com.finly.core.ui.model.TransactionCategory.fromId(cat.category).displayName }
            )
        }

        if (categorySpendItems.isNotEmpty()) {
            com.finly.core.ui.components.CategoryDonutChart(
                items = categorySpendItems,
                totalSpend = state.totalExpenses
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories & Spends",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${state.categoryBreakdown.size} Categories",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMutedDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                state.categoryBreakdown.forEach { cat ->
                    val catObj = com.finly.core.ui.model.TransactionCategory.fromId(cat.category)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleDrillDownSheet(true, cat.category) }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = catObj.icon,
                                    contentDescription = null,
                                    tint = catObj.color,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = catObj.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${CurrencyFormatter.formatInr(cat.amount)} (${cat.percentage.toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF232A4D))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((cat.percentage / 100f).coerceIn(0.02f, 1.0f))
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(catObj.color)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic 5-Factor Health Score Breakdown Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Score Factors Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                score?.let { s ->
                    FactorBar(title = "Savings Ratio (25%)", score = s.savingsRatioScore, color = PrimaryIndigo)
                    FactorBar(title = "Spending Consistency (20%)", score = s.spendingConsistencyScore, color = AccentPurple)
                    FactorBar(title = "Emergency Reserve (20%)", score = s.emergencyFundScore, color = Color(0xFF06B6D4))
                    FactorBar(title = "Debt-to-Income (20%)", score = s.debtRatioScore, color = Color(0xFF10B981))
                    FactorBar(title = "Subscription Waste (15%)", score = s.subscriptionWasteScore, color = ScoreNeedsWork)
                } ?: run {
                    Text(
                        text = "No score factors calculated yet. Parse transactions or grant notification listener permission to compute your 5-pillar health score.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subscription Radar Section
        Text(
            text = "Subscription Radar",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Detected recurring payments across bank & UPI notifications",
            style = MaterialTheme.typography.labelMedium,
            color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.subscriptions.isEmpty()) {
            Text(
                text = "No recurring subscriptions detected!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            state.subscriptions.forEach { sub ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardNavy)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Subscriptions,
                                contentDescription = null,
                                tint = AccentPurple
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = sub.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "₹${sub.amount.toInt()} / ${sub.cadence}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.toggleSubscriptionUnwanted(sub.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sub.isUnwanted) Color(0xFF3B1E2B) else Color(0xFF1E3B2B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (sub.isUnwanted) Icons.Rounded.Block else Icons.Rounded.CheckCircle,
                                    contentDescription = if (sub.isUnwanted) "Unwanted" else "Active",
                                    tint = if (sub.isUnwanted) ScoreNeedsWork else ScoreExcellent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (sub.isUnwanted) "Unwanted" else "Active",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (sub.isUnwanted) ScoreNeedsWork else ScoreExcellent,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Expense Drill-Down Modal Bottom Sheet
    if (state.isDrillDownOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleDrillDownSheet(false) },
            sheetState = sheetState,
            containerColor = DeepNavy,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = state.selectedCategoryFilter?.let { "$it Transactions" } ?: "Parsed Expenses & Transactions",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% On-Device Parsed SMS & Bank Notifications",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMutedDark
                        )
                    }

                    IconButton(onClick = { viewModel.toggleDrillDownSheet(false) }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val filteredTx = state.transactions.filter { tx ->
                    state.selectedCategoryFilter == null || tx.categoryId.equals(state.selectedCategoryFilter, ignoreCase = true)
                }

                if (filteredTx.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions found for this category.",
                            color = TextSecondaryDark,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredTx) { tx ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTransactionForDetail = tx },
                                colors = CardDefaults.cardColors(containerColor = CardNavy),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (tx.direction == TransactionDirection.CREDIT) Color(0xFF1E3B2B) else Color(0xFF3B1E2B)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (tx.direction == TransactionDirection.CREDIT) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
                                                contentDescription = null,
                                                tint = if (tx.direction == TransactionDirection.CREDIT) ScoreExcellent else ScoreNeedsWork,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = tx.merchant ?: tx.categoryId,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = tx.categoryId,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = PrimaryIndigo,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                if (tx.isExcludedFromExpenses) {
                                                    Text(
                                                        text = " · Excluded",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ScoreNeedsWork,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Text(
                                                    text = " · ${tx.sourceApp}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMutedDark
                                                )
                                            }
                                            Text(
                                                text = dateFormat.format(Date(tx.timestamp)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMutedDark
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${if (tx.direction == TransactionDirection.CREDIT) "+" else "-"} ${CurrencyFormatter.formatInr(tx.amount)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (tx.direction == TransactionDirection.CREDIT) ScoreExcellent else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedTransactionForDetail?.let { selectedTx ->
        com.finly.core.ui.components.TransactionDetailSheet(
            transaction = selectedTx,
            onSaveDetails = { categoryId, isExcluded, notes ->
                viewModel.updateTransactionDetails(selectedTx.id, categoryId, isExcluded, notes)
            },
            onDeleteTransaction = { id ->
                viewModel.deleteTransaction(id)
            },
            onDismiss = { selectedTransactionForDetail = null }
        )
    }
}

@Composable
fun FactorBar(title: String, score: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryDark,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Text(
                text = "$score / 100",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF232A4D))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
