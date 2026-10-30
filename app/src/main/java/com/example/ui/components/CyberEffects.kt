package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextZinc500

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif,
        color = Color.White
    )
) {
    val transition = rememberInfiniteTransition(label = "glitch_transition")
    val glitchOffset by transition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch_offset"
    )

    Box(modifier = modifier) {
        // Cyan offset layer
        Text(
            text = text,
            style = style.copy(color = SimaxSignal.copy(alpha = 0.45f)),
            modifier = Modifier.offset(x = (-glitchOffset).dp, y = 0.5.dp)
        )
        // Red offset layer
        Text(
            text = text,
            style = style.copy(color = SimaxBlood.copy(alpha = 0.6f)),
            modifier = Modifier.offset(x = glitchOffset.dp, y = (-0.5).dp)
        )
        // Primary text
        Text(
            text = text,
            style = style
        )
    }
}

@Composable
fun ThinkingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "think_dots")
    val alpha1 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, delayMillis = 0), RepeatMode.Reverse),
        label = "d1"
    )
    val alpha2 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, delayMillis = 200), RepeatMode.Reverse),
        label = "d2"
    )
    val alpha3 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, delayMillis = 400), RepeatMode.Reverse),
        label = "d3"
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SimaxBlood2.copy(alpha = alpha1)))
        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SimaxBlood2.copy(alpha = alpha2)))
        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SimaxBlood2.copy(alpha = alpha3)))

        Text(
            text = "Thinking...",
            color = SimaxTextZinc500,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
fun WebSearchIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "search_orbit")
    val rot by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "orbit_rot"
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val rad = size.width * 0.4f
                val angleRad = Math.toRadians(rot.toDouble())
                val dotX = center.x + (rad * Math.cos(angleRad)).toFloat()
                val dotY = center.y + (rad * Math.sin(angleRad)).toFloat()

                drawCircle(color = SimaxBlood.copy(alpha = 0.3f), radius = rad, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                drawCircle(color = SimaxBlood2, radius = 3.dp.toPx(), center = Offset(dotX, dotY))
                drawCircle(color = SimaxSignal, radius = 2.dp.toPx(), center = Offset(center.x - (dotX - center.x), center.y - (dotY - center.y)))
            }
        }

        Text(
            text = "Searching the web...",
            color = SimaxTextZinc500,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CyberGridBackground(modifier: Modifier = Modifier, isDark: Boolean = true) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 44.dp.toPx()
        val lineColor = if (isDark) SimaxBlood.copy(alpha = 0.045f) else SimaxBlood.copy(alpha = 0.03f)

        var x = 0f
        while (x < size.width) {
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += step
        }

        var y = 0f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += step
        }
    }
}
