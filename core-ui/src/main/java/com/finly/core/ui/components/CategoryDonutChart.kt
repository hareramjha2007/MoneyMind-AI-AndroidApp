package com.finly.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finly.core.ui.model.TransactionCategory
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark
import com.finly.core.ui.utils.CurrencyFormatter

data class CategorySpendItem(
    val category: TransactionCategory,
    val totalSpend: Double,
    val count: Int
)

@Composable
fun CategoryDonutChart(
    items: List<CategorySpendItem>,
    totalSpend: Double,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty() || totalSpend <= 0) return

    val sortedItems = items.sortedByDescending { it.totalSpend }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut Canvas
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    var startAngle = -90f
                    val strokeWidth = 24f

                    sortedItems.forEach { item ->
                        val sweepAngle = ((item.totalSpend / totalSpend) * 360f).toFloat()
                        if (sweepAngle > 0) {
                            drawArc(
                                color = item.category.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 2f, // Subtle gap
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Spends",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                    Text(
                        text = CurrencyFormatter.formatInr(totalSpend),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Category Legend Grid
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sortedItems.take(5).forEach { item ->
                    val percentage = (item.totalSpend / totalSpend) * 100.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(item.category.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.category.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondaryDark,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = String.format("%.1f%%", percentage),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
