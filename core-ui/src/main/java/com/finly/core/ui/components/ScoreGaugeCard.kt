package com.finly.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.ScoreFair
import com.finly.core.ui.theme.ScoreGood
import com.finly.core.ui.theme.ScoreNeedsWork
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

@Composable
fun ScoreGaugeCard(
    score: Int,
    modifier: Modifier = Modifier,
    scoreDate: String = "Today",
    onCardClick: () -> Unit = {}
) {
    val animatedScoreProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "scoreProgress"
    )

    val (scoreColor, scoreLabel) = when {
        score >= 80 -> ScoreExcellent to "Optimal"
        score >= 65 -> ScoreGood to "Strong"
        score >= 50 -> ScoreFair to "Balanced"
        else -> ScoreNeedsWork to "Needs Focus"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Health",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondaryDark
                )
                Text(
                    text = scoreDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMutedDark
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gauge Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    // Track circle
                    drawArc(
                        color = Color(0xFF232A4D),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress arc with gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(PrimaryIndigo, AccentPurple, scoreColor)
                        ),
                        startAngle = 135f,
                        sweepAngle = 270f * animatedScoreProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1.5).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = scoreLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = scoreColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap for full 5-factor score breakdown",
                style = MaterialTheme.typography.labelMedium,
                color = TextMutedDark
            )
        }
    }
}
