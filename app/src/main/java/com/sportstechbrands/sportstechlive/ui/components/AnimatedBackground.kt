package com.sportstechbrands.sportstechlive.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.sportstechbrands.sportstechlive.ui.theme.*
import kotlin.math.*

/**
 * Animated liquid-glass background.
 *
 * Renders three slow-moving radial "orbs" in accent colours on top of the
 * deep-space gradient, giving the impression of light refracting through glass.
 */
@Composable
fun AnimatedBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")

    // Each orb gets its own phase so they move independently
    val orb1X by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "orb1x"
    )
    val orb1Y by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            tween(9000, easing = LinearEasing), RepeatMode.Reverse
        ), label = "orb1y"
    )
    val orb2X by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            tween(15000, easing = LinearOutSlowInEasing), RepeatMode.Reverse
        ), label = "orb2x"
    )
    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            tween(11000, easing = FastOutLinearInEasing), RepeatMode.Reverse
        ), label = "orb2y"
    )
    val orb3X by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(13000, easing = LinearEasing), RepeatMode.Reverse
        ), label = "orb3x"
    )
    val orb3Y by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "orb3y"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SpaceBlack, SpaceNavy, SpaceDark)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOrb(
                cx = orb1X * size.width,
                cy = orb1Y * size.height,
                radius = size.width * 0.55f,
                color = AccentCyan,
                alpha = 0.08f
            )
            drawOrb(
                cx = orb2X * size.width,
                cy = orb2Y * size.height,
                radius = size.width * 0.50f,
                color = AccentPurple,
                alpha = 0.10f
            )
            drawOrb(
                cx = orb3X * size.width,
                cy = orb3Y * size.height,
                radius = size.width * 0.45f,
                color = AccentPink,
                alpha = 0.07f
            )
        }
    }
}

private fun DrawScope.drawOrb(
    cx: Float, cy: Float, radius: Float, color: Color, alpha: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = Offset(cx, cy),
            radius = radius
        ),
        radius = radius,
        center = Offset(cx, cy)
    )
}
