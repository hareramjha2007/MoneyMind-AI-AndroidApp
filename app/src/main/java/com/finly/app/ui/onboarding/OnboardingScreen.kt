package com.finly.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.ui.platform.LocalContext
import com.finly.core.ui.utils.PermissionUtils
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.finly.core.ui.components.HastradarHeaderBranding
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

import androidx.hilt.navigation.compose.hiltViewModel
import com.finly.core.domain.model.UserFinancialProfile

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 6

    var incomeInput by remember { mutableStateOf("85000") }
    var emergencyFundInput by remember { mutableStateOf("240000") }
    var hasEmergencyFund by remember { mutableStateOf(true) }

    var healthInsuranceType by remember { mutableStateOf("Corporate + Personal") }
    var healthCoverageAmount by remember { mutableStateOf("1000000") }

    var hasTermInsurance by remember { mutableStateOf(true) }
    var termCoverAmount by remember { mutableStateOf("10000000") }

    var monthlyEmiInput by remember { mutableStateOf("12000") }

    val handleComplete = {
        val profile = UserFinancialProfile(
            monthlyIncome = incomeInput.toDoubleOrNull() ?: 85000.0,
            emergencyFundAmount = if (hasEmergencyFund) emergencyFundInput.toDoubleOrNull() ?: 0.0 else 0.0,
            hasHealthInsurance = healthInsuranceType != "No Policy Yet",
            healthInsuranceCover = if (healthInsuranceType != "No Policy Yet") healthCoverageAmount.toDoubleOrNull() ?: 0.0 else 0.0,
            hasTermInsurance = hasTermInsurance,
            termInsuranceCover = if (hasTermInsurance) termCoverAmount.toDoubleOrNull() ?: 0.0 else 0.0,
            monthlyEmi = monthlyEmiInput.toDoubleOrNull() ?: 0.0
        )
        viewModel.completeOnboarding(profile)
        onOnboardingComplete()
    }

    val handleSkip = {
        viewModel.skipOnboarding()
        onOnboardingComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step Header Bar: Clean step tracker & Skip button
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $step of $totalSteps",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = handleSkip,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Skip >",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reassuring Privacy Trust Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E2648))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Encrypted Storage",
                        tint = ScoreExcellent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Encrypted & Local Vault • Zero Data Selling",
                        style = MaterialTheme.typography.labelSmall,
                        color = ScoreExcellent,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Step Animated Content Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = step,
                label = "onboardingStep"
            ) { currentStep ->
                when (currentStep) {
                    1 -> PrivacyWelcomeStep()
                    2 -> SalaryQuestionStep(incomeInput = incomeInput, onIncomeChange = { incomeInput = it })
                    3 -> EmergencyFundStep(
                        hasFund = hasEmergencyFund,
                        onHasFundChange = { hasEmergencyFund = it },
                        fundInput = emergencyFundInput,
                        onFundInputChange = { emergencyFundInput = it }
                    )
                    4 -> HealthInsuranceStep(
                        selectedType = healthInsuranceType,
                        onTypeSelected = { healthInsuranceType = it },
                        coverageInput = healthCoverageAmount,
                        onCoverageChange = { healthCoverageAmount = it }
                    )
                    5 -> TermInsuranceStep(
                        hasCover = hasTermInsurance,
                        onHasCoverChange = { hasTermInsurance = it },
                        coverInput = termCoverAmount,
                        onCoverChange = { termCoverAmount = it }
                    )
                    6 -> EmiObligationsStep(emiInput = monthlyEmiInput, onEmiChange = { monthlyEmiInput = it })
                    else -> PrivacyWelcomeStep()
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Controls with fixed Back button width
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (step > 1) {
                Button(
                    onClick = { step-- },
                    modifier = Modifier
                        .width(96.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardNavy),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Back", color = TextSecondaryDark, fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
            }

            Button(
                onClick = {
                    if (step < totalSteps) step++ else handleComplete()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (step < totalSteps) "Continue" else "Launch MoneyMind AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun PrivacyWelcomeStep() {
    val context = LocalContext.current
    val isPermissionEnabled = remember { PermissionUtils.isNotificationListenerEnabled(context) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        com.finly.core.ui.components.HastradarEmblem(size = 56.dp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "MoneyMind AI",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "BY HASTRADAR",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF00E5FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Understand your money.\nImprove your future.",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔔 1-Tap Notification Listener Permission Prompt Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isPermissionEnabled) Color(0xFF064E3B) else Color(0xFF1E1B4B)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPermissionEnabled) Icons.Rounded.CheckCircle else Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = if (isPermissionEnabled) ScoreExcellent else Color(0xFF38BDF8),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPermissionEnabled) "Automated Tracking Active" else "Enable Passive Tracking Access",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPermissionEnabled) "Notification Access is granted & active" else "Grant access to auto-track bank & UPI notifications",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                if (!isPermissionEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            PermissionUtils.openNotificationListenerSettings(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Enable Access in Settings ⚡",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                PrivacyBullet(
                    title = "🔒 100% Private SQLCipher Vault",
                    body = "Your numbers stay on your phone. Stored in local 256-bit AES encrypted database. Zero cloud syncing."
                )
                Spacer(modifier = Modifier.height(10.dp))
                PrivacyBullet(
                    title = "🛡️ Zero Sales or Ad Promotion",
                    body = "We never sell your data to brokers, advertisers, or loan apps. Your financial figures are strictly yours."
                )
                Spacer(modifier = Modifier.height(10.dp))
                PrivacyBullet(
                    title = "⚡ Automated Notification Engine",
                    body = "Parses bank & UPI notifications in RAM locally so you never log an expense manually."
                )
            }
        }
    }
}

@Composable
fun PrivacyBullet(title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = PrimaryIndigo,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        }
    }
}

@Composable
fun SalaryQuestionStep(incomeInput: String, onIncomeChange: (String) -> Unit) {
    val presets = listOf("₹50,000", "₹85,000", "₹1,20,000", "₹2,00,000", "₹3,50,000")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Rounded.Payments,
            contentDescription = null,
            tint = AccentPurple,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Monthly In-Hand Salary",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "What is your estimated monthly net take-home income?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presets) { preset ->
                val numeric = preset.replace("₹", "").replace(",", "")
                val isSelected = incomeInput == numeric
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .clickable { onIncomeChange(numeric) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = incomeInput,
            onValueChange = onIncomeChange,
            label = { Text("Monthly In-Hand Income (₹)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardNavy,
                unfocusedContainerColor = CardNavy,
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = Color(0xFF2E365C),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun EmergencyFundStep(
    hasFund: Boolean,
    onHasFundChange: (Boolean) -> Unit,
    fundInput: String,
    onFundInputChange: (String) -> Unit
) {
    val presets = listOf("₹1,00,000", "₹2,40,000", "₹3,00,000", "₹5,00,000")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Rounded.Shield,
            contentDescription = null,
            tint = Color(0xFF06B6D4),
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Emergency Cash Reserves",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Do you have liquid savings set aside for job or medical emergencies?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (hasFund) PrimaryIndigo else CardNavy)
                    .clickable { onHasFundChange(true) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Yes, I Have Savings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!hasFund) PrimaryIndigo else CardNavy)
                    .clickable { onHasFundChange(false) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Not Yet / Building",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (hasFund) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { preset ->
                    val numeric = preset.replace("₹", "").replace(",", "")
                    val isSelected = fundInput == numeric
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AccentPurple else CardNavy)
                            .clickable { onFundInputChange(numeric) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = fundInput,
                onValueChange = onFundInputChange,
                label = { Text("Emergency Fund Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy,
                    focusedBorderColor = PrimaryIndigo,
                    unfocusedBorderColor = Color(0xFF2E365C),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }
}

@Composable
fun HealthInsuranceStep(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    coverageInput: String,
    onCoverageChange: (String) -> Unit
) {
    val types = listOf("Corporate Only", "Personal Family Policy", "Corporate + Personal", "No Policy Yet")
    val presets = listOf("₹5 Lakhs", "₹10 Lakhs", "₹25 Lakhs", "₹50 Lakhs+")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Rounded.HealthAndSafety,
            contentDescription = null,
            tint = ScoreExcellent,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Health Insurance Cover",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Are you and your family protected with a Health Insurance plan?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        types.forEach { type ->
            val isSelected = selectedType == type
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onTypeSelected(type) },
                colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryIndigo else CardNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else TextMutedDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        if (selectedType != "No Policy Yet") {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { preset ->
                    val numeric = when (preset) {
                        "₹5 Lakhs" -> "500000"
                        "₹10 Lakhs" -> "1000000"
                        "₹25 Lakhs" -> "2500000"
                        else -> "5000000"
                    }
                    val isSelected = coverageInput == numeric
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AccentPurple else CardNavy)
                            .clickable { onCoverageChange(numeric) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TermInsuranceStep(
    hasCover: Boolean,
    onHasCoverChange: (Boolean) -> Unit,
    coverInput: String,
    onCoverChange: (String) -> Unit
) {
    val presets = listOf("₹50 Lakhs", "₹1 Crore", "₹2 Crores", "₹5 Crores")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Rounded.VolunteerActivism,
            contentDescription = null,
            tint = AccentPurple,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Term Life Insurance",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Do you have a Term Insurance policy to secure your family's financial future?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (hasCover) PrimaryIndigo else CardNavy)
                    .clickable { onHasCoverChange(true) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Yes, Term Covered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!hasCover) PrimaryIndigo else CardNavy)
                    .clickable { onHasCoverChange(false) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Policy Yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (hasCover) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { preset ->
                    val numeric = when (preset) {
                        "₹50 Lakhs" -> "5000000"
                        "₹1 Crore" -> "10000000"
                        "₹2 Crores" -> "20000000"
                        else -> "50000000"
                    }
                    val isSelected = coverInput == numeric
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AccentPurple else CardNavy)
                            .clickable { onCoverChange(numeric) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmiObligationsStep(emiInput: String, onEmiChange: (String) -> Unit) {
    val presets = listOf("₹0 (Debt Free)", "₹10,000", "₹25,000", "₹50,000")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Monthly Fixed EMIs & Loans",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "What is your total monthly EMI for Home, Car, Personal, or Credit Card loans?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presets) { preset ->
                val numeric = when (preset) {
                    "₹0 (Debt Free)" -> "0"
                    "₹10,000" -> "10000"
                    "₹25,000" -> "25000"
                    else -> "50000"
                }
                val isSelected = emiInput == numeric
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .clickable { onEmiChange(numeric) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emiInput,
            onValueChange = onEmiChange,
            label = { Text("Total Monthly Loan EMIs (₹)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardNavy,
                unfocusedContainerColor = CardNavy,
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = Color(0xFF2E365C),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
