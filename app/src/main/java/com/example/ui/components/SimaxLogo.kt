package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxCrimson
import com.example.ui.theme.SimaxLine

@Composable
fun SimaxLogo(
    size: Dp = 32.dp,
    modifier: Modifier = Modifier,
    hasGlow: Boolean = true,
    tint: Color? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (hasGlow) pulseScale else 1f),
        contentAlignment = Alignment.Center
    ) {
        if (hasGlow) {
            // Neon red cyber aura halo behind the emblem
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SimaxBlood.copy(alpha = 0.35f),
                                SimaxCrimson.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_simax_logo_transparent),
            contentDescription = "SIMAX AI Official Emblem",
            modifier = Modifier.size(size),
            colorFilter = tint?.let { ColorFilter.tint(it) }
        )
    }
}

@Composable
fun SimaxAvatarBadge(
    size: Dp = 36.dp,
    modifier: Modifier = Modifier,
    isModel: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isModel) 6.dp else 0.dp,
                shape = CircleShape,
                ambientColor = SimaxBlood,
                spotColor = SimaxBlood
            )
            .clip(CircleShape)
            .background(
                if (isModel) {
                    Brush.radialGradient(
                        listOf(
                            SimaxBlood.copy(alpha = 0.25f),
                            Color(0xFF0E0E0E)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF222222),
                            Color(0xFF141414)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (isModel) SimaxBlood.copy(alpha = 0.6f) else SimaxLine,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isModel) {
            SimaxLogo(size = size * 0.72f, hasGlow = false)
        } else {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                contentDescription = "User",
                modifier = Modifier.size(size * 0.5f),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}
