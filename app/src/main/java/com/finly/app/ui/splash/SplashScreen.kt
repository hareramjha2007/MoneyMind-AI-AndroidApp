package com.finly.app.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finly.core.ui.components.HastradarEmblem
import com.finly.core.ui.theme.AccentCyan
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scaleState by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "emblemScale"
    )

    val alphaState by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )

    // Pulsing background glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "haloPulse")
    val haloPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )
    val haloPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onSplashFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        // Pulsing Ambient Glow Behind Emblem
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(haloPulseScale)
                .alpha(haloPulseAlpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryIndigo,
                            AccentCyan.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Metallic Hastradar Emblem
            Box(
                modifier = Modifier.scale(scaleState),
                contentAlignment = Alignment.Center
            ) {
                HastradarEmblem(size = 110.dp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name & Company Attribution
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(alphaState)
            ) {
                Text(
                    text = "MoneyMind AI",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "BY HASTRADAR",
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentCyan,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tagline / Slogan Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CardNavy,
                                    PrimaryIndigo.copy(alpha = 0.35f),
                                    CardNavy
                                )
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "\"Understand your money. Improve your future.\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Passively track, score, and optimize your financial life.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom On-Device Security Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(alphaState)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardNavy.copy(alpha = 0.8f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "100% On-Device Financial Engine",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
