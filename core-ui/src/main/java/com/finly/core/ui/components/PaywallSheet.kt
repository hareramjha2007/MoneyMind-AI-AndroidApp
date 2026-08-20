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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.finly.core.domain.billing.SubscriptionPlan
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

@Composable
fun PaywallSheet(
    selectedPlan: SubscriptionPlan = SubscriptionPlan.ANNUAL,
    onPlanSelected: (SubscriptionPlan) -> Unit = {}
) {
    var activePlan by remember { mutableStateOf(selectedPlan) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Rounded.Star, contentDescription = null, tint = AccentPurple)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Upgrade MoneyMind AI",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Unlock unlimited AI coaching, goal forecasting, and automated subscription radar.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Subscription Plans Options
        val plans = SubscriptionPlan.values()

        plans.forEach { plan ->
            val isSelected = activePlan == plan

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryIndigo else Color(0xFF2E365C),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        activePlan = plan
                        onPlanSelected(plan)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1E264D) else CardNavy
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            activePlan = plan
                            onPlanSelected(plan)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = PrimaryIndigo,
                            unselectedColor = TextMutedDark
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = plan.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            if (plan.discountPercent > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (plan.isBestValue) AccentPurple else ScoreExcellent)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (plan.isBestValue) "SAVE ${plan.discountPercent}% • BEST VALUE" else "SAVE ${plan.discountPercent}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = plan.billingPeriod,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondaryDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${plan.totalPrice}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${plan.monthlyEquivalent}/mo",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onPlanSelected(activePlan) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Subscribe Now • ₹${activePlan.totalPrice}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
