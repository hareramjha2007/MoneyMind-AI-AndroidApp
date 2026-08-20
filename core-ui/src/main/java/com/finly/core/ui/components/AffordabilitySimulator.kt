package com.finly.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finly.core.domain.model.UserFinancialProfile
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.ScoreNeedsWork
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffordabilitySimulatorSheet(
    userProfile: UserFinancialProfile = UserFinancialProfile(),
    currentScore: Int = 85,
    onDismiss: () -> Unit,
    onAddGoal: (String, Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var itemName by remember { mutableStateOf("MacBook Pro M3") }
    var priceInput by remember { mutableStateOf("149900") }

    val presetItems = listOf(
        Pair("MacBook Pro M3", 149900.0),
        Pair("iPhone 16 Pro", 129900.0),
        Pair("Electric Vehicle", 120000.0),
        Pair("Dream Vacation", 75000.0)
    )

    val itemPrice = priceInput.toDoubleOrNull() ?: 0.0

    // Affordability Calculations
    val monthlyIncome = userProfile.monthlyIncome.coerceAtLeast(50000.0)
    val monthlyExpenses = if (userProfile.monthlyIncome > 0) (userProfile.monthlyIncome * 0.29) else 15000.0
    val monthlySavings = (monthlyIncome - monthlyExpenses).coerceAtLeast(10000.0)
    val emergencyFund = userProfile.emergencyFundAmount.coerceAtLeast(100000.0)

    val currentReserveRunwayMonths = (emergencyFund / monthlyExpenses).coerceAtLeast(0.5)
    val newEmergencyFund = (emergencyFund - itemPrice).coerceAtLeast(0.0)
    val newReserveRunwayMonths = (newEmergencyFund / monthlyExpenses).coerceAtLeast(0.0)

    val scoreDrop = when {
        itemPrice == 0.0 -> 0
        newReserveRunwayMonths >= 6.0 -> 3
        newReserveRunwayMonths >= 3.0 -> 11
        else -> 22
    }
    val projectedScore = (currentScore - scoreDrop).coerceIn(10, 100)

    val (verdictTitle, verdictColor, verdictIcon) = when {
        itemPrice == 0.0 -> Triple("Enter price to simulate", PrimaryIndigo, Icons.Rounded.Info)
        newReserveRunwayMonths >= 6.0 && itemPrice <= (monthlySavings * 2) -> Triple("✅ Safe to Purchase", ScoreExcellent, Icons.Rounded.CheckCircle)
        newReserveRunwayMonths >= 3.0 -> Triple("⚠️ Moderate Impact (Wait 30 Days)", AccentPurple, Icons.Rounded.Warning)
        else -> Triple("🚨 High Risk: Drains Emergency Reserve", ScoreNeedsWork, Icons.Rounded.Warning)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Calculate,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Can I Afford This?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }

            Text(
                text = "Simulate financial health impact & emergency reserve runway before buying.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Preset Chips
            Text(text = "Popular Choices:", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presetItems.take(3).forEach { (presetName, presetPrice) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardNavy)
                            .clickable {
                                itemName = presetName
                                priceInput = presetPrice.toInt().toString()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = presetName, style = MaterialTheme.typography.labelSmall, color = PrimaryIndigo, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item / Purchase Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = priceInput,
                onValueChange = { priceInput = it },
                label = { Text("Price in Rupees (₹)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Verdict Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, verdictColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = verdictIcon, contentDescription = null, tint = verdictColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = verdictTitle, style = MaterialTheme.typography.titleMedium, color = verdictColor, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Impact Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Health Score Impact
                        Column {
                            Text(text = "Health Score", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$currentScore", style = MaterialTheme.typography.titleMedium, color = TextSecondaryDark)
                                Text(text = " ➔ ", style = MaterialTheme.typography.titleMedium, color = TextMutedDark)
                                Text(text = "$projectedScore", style = MaterialTheme.typography.titleMedium, color = verdictColor, fontWeight = FontWeight.Bold)
                                if (scoreDrop > 0) {
                                    Text(text = " (-$scoreDrop)", style = MaterialTheme.typography.labelSmall, color = ScoreNeedsWork)
                                }
                            }
                        }

                        // Emergency Runway Impact
                        Column {
                            Text(text = "Reserve Runway", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = String.format("%.1f mo", currentReserveRunwayMonths), style = MaterialTheme.typography.titleMedium, color = TextSecondaryDark)
                                Text(text = " ➔ ", style = MaterialTheme.typography.titleMedium, color = TextMutedDark)
                                Text(text = String.format("%.1f mo", newReserveRunwayMonths), style = MaterialTheme.typography.titleMedium, color = verdictColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (itemPrice > 0) {
                        onAddGoal(itemName, itemPrice)
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "🎯 Set as Savings Goal Instead", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "Close Simulator", color = TextSecondaryDark)
            }
        }
    }
}
