package com.finly.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finly.core.ui.theme.AccentCyan
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark

/**
 * Hastradar Monogram Emblem Component
 * Renders the official Hastradar metallic emblem (Deep Navy H, Emerald Green C, Cyan S & sweeping arcs)
 */
@Composable
fun HastradarEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val width = this.size.width
        val height = this.size.height
        val center = Offset(width / 2f, height / 2f)

        // Arc Gradients
        val navyGradient = Brush.sweepGradient(
            listOf(Color(0xFF0F1E3D), Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF0F1E3D))
        )
        val greenTealGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF0D9488)),
            start = Offset(0f, height),
            end = Offset(width, 0f)
        )
        val greenGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF34D399), Color(0xFF059669))
        )

        val strokeWidth = width * 0.075f

        // 1. Outer Deep Navy Sweeping Top Arc
        drawArc(
            brush = navyGradient,
            startAngle = 180f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(width * 0.05f, height * 0.05f),
            size = Size(width * 0.9f, height * 0.9f)
        )

        // 2. Outer Emerald Sweeping Bottom Right Arc
        drawArc(
            brush = greenTealGradient,
            startAngle = 0f,
            sweepAngle = 150f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(width * 0.08f, height * 0.08f),
            size = Size(width * 0.84f, height * 0.84f)
        )

        // 3. Monogram 'H' Left Pillar & Bar
        val hPath = Path().apply {
            // Left Serif Pillar
            moveTo(width * 0.16f, height * 0.28f)
            lineTo(width * 0.28f, height * 0.28f)
            moveTo(width * 0.22f, height * 0.28f)
            lineTo(width * 0.22f, height * 0.74f)
            moveTo(width * 0.16f, height * 0.74f)
            lineTo(width * 0.28f, height * 0.74f)
            // Cross Bar
            moveTo(width * 0.22f, height * 0.50f)
            lineTo(width * 0.42f, height * 0.50f)
        }
        drawPath(
            path = hPath,
            brush = navyGradient,
            style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Square)
        )

        // 4. Monogram 'C' Inner Green Curve
        drawArc(
            brush = greenGradient,
            startAngle = 100f,
            sweepAngle = 160f,
            useCenter = false,
            style = Stroke(width = strokeWidth * 0.85f, cap = StrokeCap.Round),
            topLeft = Offset(width * 0.32f, height * 0.28f),
            size = Size(width * 0.40f, height * 0.46f)
        )

        // 5. Monogram 'S' Teal Swirl
        val sPath = Path().apply {
            moveTo(width * 0.72f, height * 0.34f)
            cubicTo(
                width * 0.50f, height * 0.24f,
                width * 0.45f, height * 0.48f,
                width * 0.62f, height * 0.54f
            )
            cubicTo(
                width * 0.78f, height * 0.60f,
                width * 0.72f, height * 0.78f,
                width * 0.48f, height * 0.74f
            )
        }
        drawPath(
            path = sPath,
            brush = greenTealGradient,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

/**
 * Full Hastradar Header Logo with App Name & Company Attribution
 */
@Composable
fun HastradarHeaderBranding(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 44.dp,
    showAttribution: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HastradarEmblem(size = emblemSize)

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "MoneyMind AI",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            if (showAttribution) {
                Text(
                    text = "BY HASTRADAR",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentCyan,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.8.sp
                )
            }
        }
    }
}
