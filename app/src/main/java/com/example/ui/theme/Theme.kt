package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SimaxDarkColorScheme = darkColorScheme(
    primary = SimaxBlood,
    onPrimary = Color.Black,
    primaryContainer = SimaxCrimson,
    onPrimaryContainer = SimaxTextWhite,
    secondary = SimaxBlood2,
    onSecondary = Color.Black,
    secondaryContainer = SimaxPanel2,
    onSecondaryContainer = SimaxBlood2,
    tertiary = SimaxEmber,
    onTertiary = Color.Black,
    background = SimaxVoid,
    onBackground = SimaxTextWhite,
    surface = SimaxPanel,
    onSurface = SimaxTextZinc200,
    surfaceVariant = SimaxPanel2,
    onSurfaceVariant = SimaxTextZinc400,
    outline = SimaxLine,
    outlineVariant = SimaxLine
)

val SimaxLightColorScheme = lightColorScheme(
    primary = SimaxLightBlood,
    onPrimary = Color.White,
    primaryContainer = SimaxLightPanel2,
    onPrimaryContainer = SimaxLightBlood,
    secondary = SimaxLightBlood2,
    onSecondary = Color.White,
    secondaryContainer = SimaxLightPanel2,
    onSecondaryContainer = SimaxLightBlood2,
    tertiary = SimaxLightEmber,
    onTertiary = Color.White,
    background = SimaxLightVoid,
    onBackground = SimaxLightText,
    surface = SimaxLightPanel,
    onSurface = SimaxLightText,
    surfaceVariant = SimaxLightPanel2,
    onSurfaceVariant = SimaxLightTextMuted,
    outline = SimaxLightLine,
    outlineVariant = SimaxLightLine
)

@Composable
fun SimaxTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SimaxDarkColorScheme else SimaxLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
