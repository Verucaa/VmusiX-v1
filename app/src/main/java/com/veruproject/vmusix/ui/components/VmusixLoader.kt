package com.veruproject.vmusix.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.veruproject.vmusix.config.BrandConfig

/**
 * VmusixLoader - Custom Modern Circular Loader.
 * Sesuai ketentuan ketat:
 * - TIDAK ADA TULISAN
 * - TIDAK ADA LOGO
 * - TIDAK ADA ANGKA
 * - TIDAK ADA PROGRESS TEXT
 * - HANYA ANIMASI lingkaran modern yang smooth, material motion, dan ringan.
 */
@Composable
fun VmusixLoader(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 3.5.dp,
    primaryColor: Color = BrandConfig.PRIMARY_COLOR,
    accentColor: Color = BrandConfig.ACCENT_COLOR
) {
    val transition = rememberInfiniteTransition(label = "vmusix_loader_anim")

    // Continuous smooth rotation (0 to 360 deg)
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing sweep arc length (from 40 to 280 deg)
    val sweepAngle by transition.animateFloat(
        initialValue = 40f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep_angle"
    )

    // Breathing scale for inner circle
    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .testTag("custom_circular_loader"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val canvasRadius = (this.size.minDimension - strokePx) / 2
            val centerOffset = Offset(this.size.width / 2, this.size.height / 2)

            // Background subtle track circle
            drawCircle(
                color = primaryColor.copy(alpha = 0.15f),
                radius = canvasRadius,
                center = centerOffset,
                style = Stroke(width = strokePx * 0.7f)
            )

            // Vibrant gradient arc
            val gradientBrush = Brush.sweepGradient(
                colors = listOf(
                    accentColor,
                    primaryColor,
                    BrandConfig.SECONDARY_COLOR,
                    accentColor
                )
            )

            drawArc(
                brush = gradientBrush,
                startAngle = rotation,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerOffset.x - canvasRadius, centerOffset.y - canvasRadius),
                size = Size(canvasRadius * 2, canvasRadius * 2),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Center subtle energy pulse dot
            drawCircle(
                color = accentColor.copy(alpha = 0.6f),
                radius = (strokePx * 0.9f) * pulseScale,
                center = centerOffset
            )
        }
    }
}
