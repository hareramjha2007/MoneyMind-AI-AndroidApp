package com.finly.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.finly.core.domain.billing.SubscriptionPlan
import com.finly.core.domain.model.UserFinancialProfile
import com.finly.core.ui.components.PaywallSheet
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.ScoreNeedsWork
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

@Composable
fun ProfileScreen(
    onStartOnboarding: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val financialProfile by viewModel.financialProfile.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.FREE_TRIAL) }
    var showPaywallSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var dataDeletedSuccess by remember { mutableStateOf(false) }

    // Dialog state for editing metrics inline
    var editingMetricTitle by remember { mutableStateOf<String?>(null) }
    var editValueInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        // Clean Title Header
        Text(
            text = "Profile & Financial Vault",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Manage your baseline financial figures & app security",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Tier Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { showPaywallSheet = true },
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = AccentPurple
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MoneyMind Premium",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedPlan == SubscriptionPlan.FREE_TRIAL) ScoreExcellent else PrimaryIndigo)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (selectedPlan == SubscriptionPlan.FREE_TRIAL) "FREE TRIAL" else "₹${selectedPlan.monthlyEquivalent}/mo",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (selectedPlan == SubscriptionPlan.FREE_TRIAL)
                        "Active Plan: 14-Day Free Trial (Full Access). Tap to view subscription plans."
                    else
                        "Active Plan: ${selectedPlan.title} (₹${selectedPlan.totalPrice}). Tap to change plan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Financial Setup Vault Data (Editable)
        Text(
            text = "Your Financial Vault Data",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Tap any metric to edit your setup responses anytime",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                VaultDataItem(
                    icon = Icons.Rounded.Payments,
                    iconTint = AccentPurple,
                    label = "Monthly In-Hand Salary",
                    value = "₹${financialProfile.monthlyIncome.toInt()}",
                    onEditClick = {
                        editingMetricTitle = "Monthly In-Hand Salary"
                        editValueInput = financialProfile.monthlyIncome.toInt().toString()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                VaultDataItem(
                    icon = Icons.Rounded.Shield,
                    iconTint = Color(0xFF06B6D4),
                    label = "Emergency Cash Reserves",
                    value = "₹${financialProfile.emergencyFundAmount.toInt()}",
                    onEditClick = {
                        editingMetricTitle = "Emergency Cash Reserves"
                        editValueInput = financialProfile.emergencyFundAmount.toInt().toString()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                VaultDataItem(
                    icon = Icons.Rounded.HealthAndSafety,
                    iconTint = ScoreExcellent,
                    label = "Health Insurance Cover",
                    value = if (financialProfile.hasHealthInsurance) "₹${financialProfile.healthInsuranceCover.toInt()}" else "No Policy",
                    onEditClick = {
                        editingMetricTitle = "Health Insurance Cover"
                        editValueInput = financialProfile.healthInsuranceCover.toInt().toString()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                VaultDataItem(
                    icon = Icons.Rounded.VolunteerActivism,
                    iconTint = AccentPurple,
                    label = "Term Life Insurance Cover",
                    value = if (financialProfile.hasTermInsurance) "₹${financialProfile.termInsuranceCover.toInt()}" else "No Policy",
                    onEditClick = {
                        editingMetricTitle = "Term Life Insurance Cover"
                        editValueInput = financialProfile.termInsuranceCover.toInt().toString()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                VaultDataItem(
                    icon = Icons.Rounded.AccountBalance,
                    iconTint = PrimaryIndigo,
                    label = "Monthly Fixed Loan EMIs",
                    value = "₹${financialProfile.monthlyEmi.toInt()}",
                    onEditClick = {
                        editingMetricTitle = "Monthly Fixed Loan EMIs"
                        editValueInput = financialProfile.monthlyEmi.toInt().toString()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Security & Privacy
        Text(
            text = "Security & App Lock",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.Fingerprint, contentDescription = null, tint = PrimaryIndigo)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Biometric App Lock", style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(text = "Protect vault with fingerprint or PIN", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                        }
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { enable ->
                            if (enable) {
                                activity?.let { act ->
                                    com.finly.core.data.security.BiometricAuthHelper.promptBiometric(
                                        activity = act,
                                        title = "Enable Biometric Lock",
                                        subtitle = "Verify identity to activate app lock",
                                        onSuccess = {
                                            viewModel.setBiometricEnabled(true)
                                            Toast.makeText(context, "Biometric Lock Activated! 🔒", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { errString ->
                                            Toast.makeText(context, "Authentication failed: $errString", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } ?: run {
                                    viewModel.setBiometricEnabled(true)
                                }
                            } else {
                                viewModel.setBiometricEnabled(false)
                                Toast.makeText(context, "Biometric Lock Disabled", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Data Management
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteConfirmDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = null, tint = ScoreNeedsWork)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Clear Local Encrypted Database", style = MaterialTheme.typography.bodyLarge, color = ScoreNeedsWork, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Inline Edit Metric Dialog
    editingMetricTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { editingMetricTitle = null },
            title = { Text(text = "Edit $title", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Enter new amount in Rupees (₹):", color = TextSecondaryDark, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editValueInput,
                        onValueChange = { editValueInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardNavy,
                            unfocusedContainerColor = CardNavy,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = editValueInput.toDoubleOrNull() ?: 0.0
                        val updated = when (title) {
                            "Monthly In-Hand Salary" -> financialProfile.copy(monthlyIncome = num)
                            "Emergency Cash Reserves" -> financialProfile.copy(emergencyFundAmount = num)
                            "Health Insurance Cover" -> financialProfile.copy(hasHealthInsurance = num > 0, healthInsuranceCover = num)
                            "Term Life Insurance Cover" -> financialProfile.copy(hasTermInsurance = num > 0, termInsuranceCover = num)
                            "Monthly Fixed Loan EMIs" -> financialProfile.copy(monthlyEmi = num)
                            else -> financialProfile
                        }
                        viewModel.updateProfile(updated)
                        editingMetricTitle = null
                        Toast.makeText(context, "$title updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMetricTitle = null }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = CardNavy
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(text = "Clear Local Data?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(text = "This will permanently wipe all transactions stored in your local Room database.", color = TextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllLocalData()
                        showDeleteConfirmDialog = false
                        dataDeletedSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScoreNeedsWork)
                ) {
                    Text("Confirm Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = CardNavy
        )
    }

    if (showPaywallSheet) {
        PaywallSheet(
            selectedPlan = selectedPlan,
            onPlanSelected = { plan ->
                selectedPlan = plan
                showPaywallSheet = false
            }
        )
    }
}

@Composable
fun VaultDataItem(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onEditClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(text = value, style = MaterialTheme.typography.labelMedium, color = PrimaryIndigo, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF232A4D))
                .padding(8.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
        }
    }
}
