package com.finly.feature.goals

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finly.core.domain.model.Goal
import com.finly.core.domain.model.GoalType
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark
import java.util.UUID

import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.IconButton

@Composable
fun GoalsScreen(
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedGoalForTip by remember { mutableStateOf<Goal?>(null) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    var newGoalTitle by remember { mutableStateOf("") }
    var newTargetAmount by remember { mutableStateOf("150000") }
    var newCurrentAmount by remember { mutableStateOf("25000") }

    val goals = remember { mutableStateListOf<Goal>() }

    val presetTitles = listOf("Emergency Reserve", "New Bike", "iPhone 16 Pro", "House Down Payment")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 6.dp)
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Financial Goals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Button(
                onClick = { showAddGoalDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Goal")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Goal", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (goals.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CardNavy)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Flag,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No financial goals set yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap '+ Add Goal' above to create your emergency fund or target purchase!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            goals.forEach { goal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardNavy)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Flag,
                                    contentDescription = null,
                                    tint = AccentPurple
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = "${goal.progressPercentage.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        val milestoneText = when {
                            goal.progressPercentage >= 100.0 -> "🏆 GOAL ACHIEVED!"
                            goal.progressPercentage >= 75.0 -> "🔥 75% Milestone Reached"
                            goal.progressPercentage >= 50.0 -> "⚡ 50% Milestone Reached"
                            goal.progressPercentage >= 25.0 -> "🌱 25% Milestone Reached"
                            else -> null
                        }

                        milestoneText?.let { badge ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (goal.progressPercentage >= 100.0) ScoreExcellent else PrimaryIndigo.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { goal.progressPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = PrimaryIndigo,
                            trackColor = Color(0xFF2E365C)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Saved: ₹${goal.currentAmount.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondaryDark
                            )
                            Text(
                                text = "Target: ₹${goal.targetAmount.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMutedDark
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF232A4D))
                                .clickable { selectedGoalForTip = goal }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentPurple,
                                    modifier = Modifier.height(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedGoalForTip?.id == goal.id)
                                        "Tip: Cutting ₹800/mo unused subscriptions speeds this up by 14 days!"
                                    else
                                        "AI projected completion: On track. Tap to see speed-up tips.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddGoalDialog) {
        val smartPresets = listOf(
            Pair("MacBook Pro M3", "149900"),
            Pair("iPhone 16 Pro", "129900"),
            Pair("Emergency Reserve", "180000"),
            Pair("Electric Vehicle", "120000"),
            Pair("Dream Vacation", "75000")
        )

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "Create New Goal",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Smart Presets (Tap to Autofill):",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(smartPresets) { (pTitle, pAmount) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2E365C))
                                    .clickable {
                                        newGoalTitle = pTitle
                                        newTargetAmount = pAmount
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = pTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        label = { Text("Goal Title") },
                        placeholder = { Text("e.g. Dream Vacation") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Color(0xFF2E365C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newTargetAmount,
                        onValueChange = { newTargetAmount = it },
                        label = { Text("Target Amount (₹)") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Color(0xFF2E365C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCurrentAmount,
                        onValueChange = { newCurrentAmount = it },
                        label = { Text("Initial Saved Amount (₹)") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy,
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
                        val title = if (newGoalTitle.isBlank()) "Custom Savings Goal" else newGoalTitle
                        val target = newTargetAmount.toDoubleOrNull() ?: 100000.0
                        val current = newCurrentAmount.toDoubleOrNull() ?: 0.0

                        goals.add(
                            Goal(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                type = GoalType.CUSTOM,
                                targetAmount = target,
                                currentAmount = current,
                                targetDate = System.currentTimeMillis() + (120L * 24 * 3600 * 1000)
                            )
                        )
                        showAddGoalDialog = false
                        newGoalTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = TextMutedDark)
                }
            }
        )
    }
}
