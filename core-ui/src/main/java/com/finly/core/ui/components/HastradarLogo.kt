package com.finly.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

/**
 * Premium Hastradar Financial AI Emblem Component
 * Combines Hastradar Orbital Radar Rings, Upward Financial Growth Arrow, and AI Spark.
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

        val strokeWidth = width * 0.08f

        // Gradients
        val radarGradient = Brush.sweepGradient(
            colors = listOf(
                Color(0xFF00F2FE), // Cyan
                Color(0xFF4FACFE), // Blue
                Color(0xFF7000FF), // Violet
                Color(0xFF00F2FE)  // Cyan
            )
        )
        val growthGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFF059669)),
            start = Offset(0f, height),
            end = Offset(width, 0f)
        )
        val sparkGradient = Brush.radialGradient(
            colors = listOf(Color(0xFFD8B4FE), Color(0xFFA855F7), Color(0xFF7E22CE)),
            center = Offset(width * 0.5f, height * 0.28f),
            radius = width * 0.25f
        )

        // 1. Outer Hastradar Concentric Pulse Rings
        drawArc(
            brush = radarGradient,
            startAngle = -30f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(width * 0.06f, height * 0.06f),
            size = Size(width * 0.88f, height * 0.88f)
        )

        drawArc(
            brush = radarGradient,
            startAngle = 140f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = strokeWidth * 0.6f, cap = StrokeCap.Round),
            topLeft = Offset(width * 0.18f, height * 0.18f),
            size = Size(width * 0.64f, height * 0.64f)
        )

        // 2. Upward Financial Growth Arrow Path
        val arrowPath = Path().apply {
            moveTo(width * 0.26f, height * 0.72f)
            lineTo(width * 0.44f, height * 0.54f)
            lineTo(width * 0.56f, height * 0.62f)
            lineTo(width * 0.76f, height * 0.36f)

            // Arrowhead tip
            moveTo(width * 0.64f, height * 0.36f)
            lineTo(width * 0.78f, height * 0.34f)
            lineTo(width * 0.76f, height * 0.50f)
        }

        drawPath(
            path = arrowPath,
            brush = growthGradient,
            style = Stroke(width = strokeWidth * 1.1f, cap = StrokeCap.Round)
        )

        // 3. AI Spark Star at Center Top
        val sparkPath = Path().apply {
            val cx = width * 0.5f
            val cy = height * 0.28f
            val rOuter = width * 0.12f

            moveTo(cx, cy - rOuter)
            quadraticBezierTo(cx, cy, cx + rOuter, cy)
            quadraticBezierTo(cx, cy, cx, cy + rOuter)
            quadraticBezierTo(cx, cy, cx - rOuter, cy)
            quadraticBezierTo(cx, cy, cx, cy - rOuter)
        }

        drawPath(
            path = sparkPath,
            brush = sparkGradient
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
