package com.finly.app.ui.review

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finly.core.data.parser.ParseResult
import com.finly.core.data.parser.TransactionParserEngine
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.repository.TransactionRepository
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionReviewViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val transactions = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun simulateNotification(message: String, sender: String) {
        viewModelScope.launch {
            val parser = TransactionParserEngine()
            when (val result = parser.parseMessage(message, senderId = sender, packageName = sender)) {
                is ParseResult.Success -> {
                    val parsed = result.transaction
                    val tx = Transaction(
                        id = UUID.randomUUID().toString(),
                        amount = parsed.amount,
                        direction = parsed.direction,
                        timestamp = System.currentTimeMillis(),
                        sourceApp = parsed.sourceApp,
                        rawSenderId = parsed.rawSenderId,
                        categoryId = parsed.categoryId,
                        merchant = parsed.merchant,
                        confidenceScore = parsed.confidenceScore
                    )
                    transactionRepository.insertTransaction(tx)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TransactionReviewScreen(
    onBackClick: () -> Unit = {},
    viewModel: TransactionReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    var showSimulatorDialog by remember { mutableStateOf(false) }
    var sampleMessageText by remember { mutableStateOf("Rs. 1,450 debited at Zomato via HDFC Bank UPI ref 987654") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Parsed Transactions (${transactions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real On-Device Notification Tracking",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMutedDark
                    )
                }
            }

            Button(
                onClick = { showSimulatorDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Simulate", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Listener Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.NotificationsActive, contentDescription = null, tint = AccentPurple)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Notification Tracker Active", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(text = "Listens to live HDFC, SBI, ICICI, UPI notifications", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                    }
                }

                TextButton(
                    onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        } catch (_: Exception) {}
                    }
                ) {
                    Text(text = "Enable", color = PrimaryIndigo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No transactions parsed yet.",
                    color = TextSecondaryDark,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.merchant ?: tx.categoryId,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF2E365C))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tx.categoryId.uppercase(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = AccentPurple,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tx.sourceApp,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextMutedDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateFormat.format(Date(tx.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (tx.direction == TransactionDirection.DEBIT) "-" else "+"}₹${tx.amount.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (tx.direction == TransactionDirection.DEBIT) Color.White else ScoreExcellent,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Confidence: ${(tx.confidenceScore * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSimulatorDialog) {
        AlertDialog(
            onDismissRequest = { showSimulatorDialog = false },
            title = {
                Text(text = "Simulate Bank Notification", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Enter any raw SMS or app notification to test MoneyMind AI's live on-device regex parser:",
                        color = TextSecondaryDark,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = sampleMessageText,
                        onValueChange = { sampleMessageText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Color(0xFF2E365C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.simulateNotification(sampleMessageText, sender = "HDFCBK")
                        showSimulatorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Parse & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulatorDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = CardNavy
        )
    }
}
