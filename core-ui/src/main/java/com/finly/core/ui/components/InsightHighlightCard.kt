package com.finly.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

@Composable
fun InsightHighlightCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.AutoAwesome,
    isAiInsight: Boolean = true,
    tag: String = "BEHAVIORAL INSIGHT"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = if (isAiInsight) listOf(PrimaryIndigo, AccentPurple) else listOf(Color(0xFF06B6D4), PrimaryIndigo)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isAiInsight) AccentPurple else Color(0xFF06B6D4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        }
    }
}
