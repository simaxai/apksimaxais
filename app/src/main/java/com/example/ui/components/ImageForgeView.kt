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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxCrimson
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxLine
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextZinc400
import kotlinx.coroutines.delay

@Composable
fun ImageForgeView(
    prompt: String,
    modifier: Modifier = Modifier
) {
    val forgeStages = remember {
        listOf("FORGING", "SYNTHESIZING", "SHADING", "REFINING", "RENDERING")
    }
    var stageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1100L)
            stageIndex = (stageIndex + 1) % forgeStages.size
        }
    }

    val transition = rememberInfiniteTransition(label = "forge_anim")
    val rotation1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "rot1"
    )
    val rotation2 by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(13000, easing = LinearEasing)),
        label = "rot2"
    )
    val scanY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        SimaxBlood.copy(alpha = 0.18f),
                        Color(0xFF0A0000),
                        Color(0xFF030303)
                    )
                )
            )
            .border(1.dp, SimaxBlood.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        // Futuristic radar / particle canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius1 = size.width * 0.35f
            val radius2 = size.width * 0.44f

            // Orbit 1: dashed red line
            drawCircle(
                color = SimaxBlood.copy(alpha = 0.45f),
                radius = radius1,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), rotation1)
                )
            )

            // Orbit 2: cyan accent line
            drawCircle(
                color = SimaxSignal.copy(alpha = 0.35f),
                radius = radius2,
                center = center,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 24f), rotation2)
                )
            )

            // Scanning laser light bar
            val currentScanY = size.height * scanY
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        SimaxBlood.copy(alpha = 0.25f),
                        SimaxBlood.copy(alpha = 0.6f),
                        SimaxBlood.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    startY = currentScanY - 40.dp.toPx(),
                    endY = currentScanY + 40.dp.toPx()
                ),
                topLeft = Offset(0f, currentScanY - 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width, 80.dp.toPx())
            )
        }

        // Central Core
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                SimaxLogo(size = 72.dp, hasGlow = true)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = forgeStages[stageIndex],
                color = SimaxBlood2,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"$prompt\"",
                color = SimaxTextZinc400,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
