package com.example.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Modern, futuristic, high-aesthetic vector icons custom-crafted for SIMAX AI.
 * Outperforms standard stock material icons with sleek curves, rounded caps,
 * and high-tech elegance exceeding ChatGPT's interface design.
 */
object ModernIcons {

    /**
     * Google "G" Icon for Authentic Sign-In
     */
    val Google: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernGoogle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20.5f, 12f)
                lineTo(12.5f, 12f)
                moveTo(20.2f, 15f)
                curveTo(19.2f, 18f, 16.2f, 20.2f, 12.5f, 20.2f)
                curveTo(7.8f, 20.2f, 4f, 16.4f, 4f, 11.7f)
                curveTo(4f, 7f, 7.8f, 3.2f, 12.5f, 3.2f)
                curveTo(15.2f, 3.2f, 17.5f, 4.4f, 19.1f, 6.3f)
            }
        }.build()
    }

    /**
     * S_TURBO_FLASH: Fast, dynamic electric bolt
     */
    val FlashTier: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernFlashTier",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE50914)),
                stroke = null
            ) {
                moveTo(13.5f, 2f)
                lineTo(5f, 13f)
                lineTo(11.5f, 13f)
                lineTo(10.5f, 22f)
                lineTo(19f, 11f)
                lineTo(12.5f, 11f)
                close()
            }
        }.build()
    }

    /**
     * S_TURBO_HIGH: Supersonic twin-turbine wings
     */
    val HighTier: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernHighTier",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFFF3333)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2.5f)
                lineTo(16f, 9f)
                lineTo(21.5f, 11f)
                lineTo(17f, 15.5f)
                lineTo(18.5f, 21.5f)
                lineTo(12f, 18f)
                lineTo(5.5f, 21.5f)
                lineTo(7f, 15.5f)
                lineTo(2.5f, 11f)
                lineTo(8f, 9f)
                close()
            }
        }.build()
    }

    /**
     * S_TURBO_MAX: Faceted high-precision diamond
     */
    val MaxTier: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernMaxTier",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFFF4D4D)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6f, 4f)
                lineTo(18f, 4f)
                lineTo(21.5f, 9.5f)
                lineTo(12f, 20.5f)
                lineTo(2.5f, 9.5f)
                close()
                moveTo(2.5f, 9.5f); lineTo(21.5f, 9.5f)
                moveTo(12f, 20.5f); lineTo(8f, 9.5f); lineTo(10f, 4f)
                moveTo(12f, 20.5f); lineTo(16f, 9.5f); lineTo(14f, 4f)
            }
        }.build()
    }

    /**
     * S_TURBO_ULTRA_MAX: Imperial Crown of Earth & Human Knowledge
     */
    val UltraMaxTier: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernUltraMaxTier",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFFF9900)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3.5f, 8f)
                lineTo(7f, 18.5f)
                lineTo(17f, 18.5f)
                lineTo(20.5f, 8f)
                lineTo(15.5f, 12f)
                lineTo(12f, 5.5f)
                lineTo(8.5f, 12f)
                close()
                moveTo(6.5f, 20.5f)
                lineTo(17.5f, 20.5f)
            }
        }.build()
    }

    /**
     * Persona: General Superintelligence
     */
    val GeneralPersona: ImageVector by lazy { Sparkle }

    /**
     * Persona: Globe / Web Researcher
     */
    val Globe: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernGlobe",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 2.5f)
                curveTo(17.25f, 2.5f, 21.5f, 6.75f, 21.5f, 12f)
                curveTo(21.5f, 17.25f, 17.25f, 21.5f, 12f, 21.5f)
                curveTo(6.75f, 21.5f, 2.5f, 17.25f, 2.5f, 12f)
                curveTo(2.5f, 6.75f, 6.75f, 2.5f, 12f, 2.5f)
                close()
                moveTo(2.8f, 12f); lineTo(21.2f, 12f)
                moveTo(12f, 2.5f)
                curveTo(8f, 6.5f, 8f, 17.5f, 12f, 21.5f)
                moveTo(12f, 2.5f)
                curveTo(16f, 6.5f, 16f, 17.5f, 12f, 21.5f)
            }
        }.build()
    }

    /**
     * Persona: Pen / Creative Writer
     */
    val Pen: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernPen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18.5f, 3.5f)
                lineTo(20.5f, 5.5f)
                curveTo(21.2f, 6.2f, 21.2f, 7.3f, 20.5f, 8f)
                lineTo(9.5f, 19f)
                lineTo(4f, 20f)
                lineTo(5f, 14.5f)
                lineTo(16f, 3.5f)
                curveTo(16.7f, 2.8f, 17.8f, 2.8f, 18.5f, 3.5f)
                close()
                moveTo(14.5f, 5f); lineTo(19f, 9.5f)
            }
        }.build()
    }

    /**
     * Persona: Shield / Cybersecurity Sentinel
     */
    val Shield: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernShield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2.5f)
                lineTo(20f, 6f)
                curveTo(20f, 14f, 16f, 19f, 12f, 21.5f)
                curveTo(8f, 19f, 4f, 14f, 4f, 6f)
                close()
                moveTo(12f, 7.5f)
                lineTo(12f, 16f)
                moveTo(9f, 11.5f)
                lineTo(15f, 11.5f)
            }
        }.build()
    }

    /**
     * Modern Photo Frame Icon
     */
    val Photo: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernPhoto",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 5f)
                lineTo(20f, 5f)
                curveTo(20.6f, 5f, 21f, 5.4f, 21f, 6f)
                lineTo(21f, 18f)
                curveTo(21f, 18.6f, 20.6f, 19f, 20f, 19f)
                lineTo(4f, 19f)
                curveTo(3.4f, 19f, 3f, 18.6f, 3f, 18f)
                lineTo(3f, 6f)
                curveTo(3.4f, 5.4f, 3.4f, 5f, 4f, 5f)
                close()
                moveTo(3.5f, 16f)
                lineTo(8.5f, 10.5f)
                lineTo(14f, 16f)
                lineTo(16.5f, 13f)
                lineTo(20.5f, 17f)
                moveTo(16.5f, 8.5f)
                curveTo(17.3f, 8.5f, 18f, 9.2f, 18f, 10f)
                curveTo(18f, 10.8f, 17.3f, 11.5f, 16.5f, 11.5f)
                curveTo(15.7f, 11.5f, 15f, 10.8f, 15f, 10f)
                curveTo(15f, 9.2f, 15.7f, 8.5f, 16.5f, 8.5f)
                close()
            }
        }.build()
    }

    /**
     * Modern Palette / Image Generation Art
     */
    val Palette: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernPalette",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2.5f)
                curveTo(6.5f, 2.5f, 2.5f, 6.5f, 2.5f, 12f)
                curveTo(2.5f, 17.5f, 6.5f, 21.5f, 11f, 21.5f)
                curveTo(12.5f, 21.5f, 13.5f, 20.3f, 13.5f, 19f)
                curveTo(13.5f, 18.2f, 13.2f, 17.5f, 12.8f, 17f)
                curveTo(12.3f, 16.5f, 12f, 15.8f, 12f, 15f)
                curveTo(12f, 13.3f, 13.3f, 12f, 15f, 12f)
                lineTo(17.5f, 12f)
                curveTo(19.7f, 12f, 21.5f, 10.2f, 21.5f, 8f)
                curveTo(21.5f, 4.9f, 17.2f, 2.5f, 12f, 2.5f)
                close()
            }
        }.build()
    }

    /**
     * Table / Grid Icon
     */
    val Table: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernTable",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3.5f, 5.5f)
                lineTo(20.5f, 5.5f)
                lineTo(20.5f, 18.5f)
                lineTo(3.5f, 18.5f)
                close()
                moveTo(3.5f, 10f); lineTo(20.5f, 10f)
                moveTo(9f, 5.5f); lineTo(9f, 18.5f)
                moveTo(15f, 5.5f); lineTo(15f, 18.5f)
            }
        }.build()
    }

    /**
     * Math Sigma / Formula Icon
     */
    val Formula: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernFormula",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 4.5f)
                lineTo(6f, 4.5f)
                lineTo(12.5f, 12f)
                lineTo(6f, 19.5f)
                lineTo(19f, 19.5f)
            }
        }.build()
    }

    /**
     * Futuristic Drawer Menu: 3 sleek terminal pills with staggered widths
     */
    val Menu: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernMenu",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Top pill
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3.5f, 6.5f)
                lineTo(20.5f, 6.5f)
            }
            // Middle shorter pill
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3.5f, 12f)
                lineTo(15f, 12f)
            }
            // Bottom pill
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3.5f, 17.5f)
                lineTo(18.5f, 17.5f)
            }
        }.build()
    }

    /**
     * Deep Reasoning / Brain Synapse Icon
     */
    val Brain: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernBrain",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Left hemisphere
                moveTo(12f, 4.5f)
                curveTo(9.5f, 4.5f, 7f, 5.5f, 6f, 7.5f)
                curveTo(4.5f, 8.5f, 4f, 11f, 5f, 13f)
                curveTo(4f, 15f, 5f, 17.5f, 7f, 18.5f)
                curveTo(8.5f, 20f, 11f, 19.5f, 12f, 19.5f)
                // Center partition
                moveTo(12f, 4.5f)
                lineTo(12f, 19.5f)
                // Right hemisphere
                moveTo(12f, 4.5f)
                curveTo(14.5f, 4.5f, 17f, 5.5f, 18f, 7.5f)
                curveTo(19.5f, 8.5f, 20f, 11f, 19f, 13f)
                curveTo(20f, 15f, 19f, 17.5f, 17f, 18.5f)
                curveTo(15.5f, 20f, 13f, 19.5f, 12f, 19.5f)
                // Neural cross nodes
                moveTo(8.5f, 10f)
                lineTo(12f, 11.5f)
                lineTo(15.5f, 10f)
                moveTo(9f, 14.5f)
                lineTo(12f, 13.5f)
                lineTo(15f, 14.5f)
            }
        }.build()
    }

    /**
     * Futuristic Sparkle / AI Star
     */
    val Sparkle: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernSparkle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f
            ) {
                moveTo(12f, 2f)
                curveTo(12f, 7.5f, 16.5f, 12f, 22f, 12f)
                curveTo(16.5f, 12f, 12f, 16.5f, 12f, 22f)
                curveTo(12f, 16.5f, 7.5f, 12f, 2f, 12f)
                curveTo(7.5f, 12f, 12f, 7.5f, 12f, 2f)
                close()
            }
        }.build()
    }

    /**
     * Futuristic Radar Web Search / Compass
     */
    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernSearch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Outer radar lens
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(10.5f, 3f)
                curveTo(14.64f, 3f, 18f, 6.36f, 18f, 10.5f)
                curveTo(18f, 14.64f, 14.64f, 18f, 10.5f, 18f)
                curveTo(6.36f, 18f, 3f, 14.64f, 3f, 10.5f)
                curveTo(3f, 6.36f, 6.36f, 3f, 10.5f, 3f)
                close()
            }
            // Handle with high-tech angle
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(16f, 16f)
                lineTo(21.5f, 21.5f)
            }
            // Central optic reticle
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(10.5f, 9.5f)
                lineTo(11.5f, 10.5f)
                lineTo(10.5f, 11.5f)
                lineTo(9.5f, 10.5f)
                close()
            }
        }.build()
    }

    /**
     * Futuristic Attachment / Paperclip with cyber loop
     */
    val Paperclip: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernPaperclip",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21.44f, 11.05f)
                lineTo(12.25f, 20.24f)
                curveTo(10.3f, 22.19f, 7.13f, 22.19f, 5.18f, 20.24f)
                curveTo(3.23f, 18.29f, 3.23f, 15.12f, 5.18f, 13.17f)
                lineTo(14.37f, 3.98f)
                curveTo(15.67f, 2.68f, 17.78f, 2.68f, 19.08f, 3.98f)
                curveTo(20.38f, 5.28f, 20.38f, 7.39f, 19.08f, 8.69f)
                lineTo(10.15f, 17.62f)
                curveTo(9.5f, 18.27f, 8.44f, 18.27f, 7.79f, 17.62f)
                curveTo(7.14f, 16.97f, 7.14f, 15.91f, 7.79f, 15.26f)
                lineTo(16.03f, 7.02f)
            }
        }.build()
    }

    /**
     * Futuristic Sonic Waveform Mic
     */
    val Mic: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernMic",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Capsule
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 3.5f)
                lineTo(12f, 11.5f)
                moveTo(9f, 6.5f)
                lineTo(9f, 9.5f)
                moveTo(15f, 6.5f)
                lineTo(15f, 9.5f)
            }
            // Acoustic cradle
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(6.5f, 10f)
                curveTo(6.5f, 13.5f, 9f, 16f, 12f, 16f)
                curveTo(15f, 16f, 17.5f, 13.5f, 17.5f, 10f)
                // Stem
                moveTo(12f, 16.5f)
                lineTo(12f, 20.5f)
                // Base
                moveTo(8.5f, 20.5f)
                lineTo(15.5f, 20.5f)
            }
        }.build()
    }

    /**
     * Aerodynamic Supersonic Jet / Send Arrow
     */
    val Send: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernSend",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                stroke = null
            ) {
                moveTo(2.5f, 3f)
                lineTo(22f, 12f)
                lineTo(2.5f, 21f)
                lineTo(5.5f, 13.5f)
                lineTo(14f, 12f)
                lineTo(5.5f, 10.5f)
                close()
            }
        }.build()
    }

    /**
     * Futuristic Stop / Rounded Holographic Cube
     */
    val Stop: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernStop",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(7f, 6f)
                lineTo(17f, 6f)
                curveTo(17.55f, 6f, 18f, 6.45f, 18f, 7f)
                lineTo(18f, 17f)
                curveTo(18f, 17.55f, 17.55f, 18f, 17f, 18f)
                lineTo(7f, 18f)
                curveTo(6.45f, 18f, 6f, 17.55f, 6f, 17f)
                lineTo(6f, 7f)
                curveTo(6f, 6.45f, 6.45f, 6f, 7f, 6f)
                close()
            }
        }.build()
    }

    /**
     * Modern Futuristic Precision Gear (Settings)
     */
    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernSettings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Central aperture
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f
            ) {
                moveTo(12f, 9.5f)
                curveTo(13.38f, 9.5f, 14.5f, 10.62f, 14.5f, 12f)
                curveTo(14.5f, 13.38f, 13.38f, 14.5f, 12f, 14.5f)
                curveTo(10.62f, 14.5f, 9.5f, 13.38f, 9.5f, 12f)
                curveTo(9.5f, 10.62f, 10.62f, 9.5f, 12f, 9.5f)
                close()
            }
            // Precision hex ring teeth
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2.5f)
                lineTo(14f, 4.5f)
                lineTo(16.5f, 4f)
                lineTo(17.5f, 6.5f)
                lineTo(20f, 7.5f)
                lineTo(19.5f, 10f)
                lineTo(21.5f, 12f)
                lineTo(19.5f, 14f)
                lineTo(20f, 16.5f)
                lineTo(17.5f, 17.5f)
                lineTo(16.5f, 20f)
                lineTo(14f, 19.5f)
                lineTo(12f, 21.5f)
                lineTo(10f, 19.5f)
                lineTo(7.5f, 20f)
                lineTo(6.5f, 17.5f)
                lineTo(4f, 16.5f)
                lineTo(4.5f, 14f)
                lineTo(2.5f, 12f)
                lineTo(4.5f, 10f)
                lineTo(4f, 7.5f)
                lineTo(6.5f, 6.5f)
                lineTo(7.5f, 4f)
                lineTo(10f, 4.5f)
                close()
            }
        }.build()
    }

    /**
     * Modern Sun / Solar Theme
     */
    val Sun: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernSun",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Solar core
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f
            ) {
                moveTo(12f, 7.5f)
                curveTo(14.48f, 7.5f, 16.5f, 9.52f, 16.5f, 12f)
                curveTo(16.5f, 14.48f, 14.48f, 16.5f, 12f, 16.5f)
                curveTo(9.52f, 16.5f, 7.5f, 14.48f, 7.5f, 12f)
                curveTo(7.5f, 9.52f, 9.52f, 7.5f, 12f, 7.5f)
                close()
            }
            // Solar rays
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 2.5f); lineTo(12f, 4.5f)
                moveTo(12f, 19.5f); lineTo(12f, 21.5f)
                moveTo(2.5f, 12f); lineTo(4.5f, 12f)
                moveTo(19.5f, 12f); lineTo(21.5f, 12f)
                moveTo(5.3f, 5.3f); lineTo(6.7f, 6.7f)
                moveTo(17.3f, 17.3f); lineTo(18.7f, 18.7f)
                moveTo(5.3f, 18.7f); lineTo(6.7f, 17.3f)
                moveTo(17.3f, 6.7f); lineTo(18.7f, 5.3f)
            }
        }.build()
    }

    /**
     * Modern Moon / Night Theme
     */
    val Moon: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernMoon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(12.3f, 2.5f)
                curveTo(7.5f, 2.8f, 3.8f, 6.7f, 3.8f, 11.6f)
                curveTo(3.8f, 16.8f, 8f, 21f, 13.2f, 21f)
                curveTo(17.8f, 21f, 21.5f, 17.7f, 22.2f, 13.3f)
                curveTo(20.9f, 14f, 19.4f, 14.4f, 17.8f, 14.4f)
                curveTo(12.7f, 14.4f, 8.6f, 10.3f, 8.6f, 5.2f)
                curveTo(8.6f, 4.2f, 8.8f, 3.3f, 9.1f, 2.4f)
                lineTo(12.3f, 2.5f)
                close()
            }
        }.build()
    }

    /**
     * Modern Multi-layered Document / Data Sheet
     */
    val Document: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernDocument",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(14f, 2.5f)
                lineTo(5.5f, 2.5f)
                curveTo(4.4f, 2.5f, 3.5f, 3.4f, 3.5f, 4.5f)
                lineTo(3.5f, 19.5f)
                curveTo(3.5f, 20.6f, 4.4f, 21.5f, 5.5f, 21.5f)
                lineTo(18.5f, 21.5f)
                curveTo(19.6f, 21.5f, 20.5f, 20.6f, 20.5f, 19.5f)
                lineTo(20.5f, 9f)
                lineTo(14f, 2.5f)
                close()
                // Folded corner
                moveTo(14f, 2.5f)
                lineTo(14f, 9f)
                lineTo(20.5f, 9f)
                // Data lines
                moveTo(7.5f, 13.5f)
                lineTo(16.5f, 13.5f)
                moveTo(7.5f, 17f)
                lineTo(13.5f, 17f)
            }
        }.build()
    }

    /**
     * Modern Code Tag / Debugger `</>`
     */
    val Code: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernCode",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7.5f, 8f)
                lineTo(3.5f, 12f)
                lineTo(7.5f, 16f)
                moveTo(16.5f, 8f)
                lineTo(20.5f, 12f)
                lineTo(16.5f, 16f)
                moveTo(13.5f, 6.5f)
                lineTo(10.5f, 17.5f)
            }
        }.build()
    }

    /**
     * Modern Data Analytics / Bar Chart
     */
    val Chart: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernChart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                // Rising bars
                moveTo(5f, 19.5f); lineTo(5f, 14f)
                moveTo(10f, 19.5f); lineTo(10f, 9f)
                moveTo(15f, 19.5f); lineTo(15f, 5f)
                moveTo(20f, 19.5f); lineTo(20f, 11f)
                // Baseline
                moveTo(2.5f, 20.5f); lineTo(21.5f, 20.5f)
            }
        }.build()
    }

    /**
     * Modern Sound Wave Voice / Live Mode
     */
    val VoiceWave: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernVoiceWave",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(4f, 12f); lineTo(4f, 12f)
                moveTo(8f, 7.5f); lineTo(8f, 16.5f)
                moveTo(12f, 4f); lineTo(12f, 20f)
                moveTo(16f, 7.5f); lineTo(16f, 16.5f)
                moveTo(20f, 12f); lineTo(20f, 12f)
            }
        }.build()
    }

    /**
     * Modern Plus / Add
     */
    val Add: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernAdd",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 4.5f); lineTo(12f, 19.5f)
                moveTo(4.5f, 12f); lineTo(19.5f, 12f)
            }
        }.build()
    }

    /**
     * Modern Copy Layered Glass
     */
    val Copy: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernCopy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Front card
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8.5f, 8.5f)
                lineTo(19f, 8.5f)
                curveTo(19.8f, 8.5f, 20.5f, 9.2f, 20.5f, 10f)
                lineTo(20.5f, 20.5f)
                curveTo(20.5f, 21.3f, 19.8f, 22f, 19f, 22f)
                lineTo(8.5f, 22f)
                curveTo(7.7f, 22f, 7f, 21.3f, 7f, 20.5f)
                lineTo(7f, 10f)
                curveTo(7f, 9.2f, 7.7f, 8.5f, 8.5f, 8.5f)
                close()
            }
            // Back card
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 16f)
                lineTo(4f, 16f)
                curveTo(3.2f, 16f, 2.5f, 15.3f, 2.5f, 14.5f)
                lineTo(2.5f, 4.5f)
                curveTo(2.5f, 3.7f, 3.2f, 3f, 4f, 3f)
                lineTo(14f, 3f)
                curveTo(14.8f, 3f, 15.5f, 3.7f, 15.5f, 4.5f)
                lineTo(15.5f, 5.5f)
            }
        }.build()
    }

    /**
     * Modern Verified Checkmark
     */
    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4.5f, 12.5f)
                lineTo(9.5f, 17.5f)
                lineTo(19.5f, 6.5f)
            }
        }.build()
    }

    /**
     * Modern Dual Orbiting Curved Arrows (Regenerate)
     */
    val Refresh: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernRefresh",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                // Top clockwise arc
                moveTo(20f, 10.5f)
                curveTo(19.2f, 6.7f, 15.9f, 3.8f, 12f, 3.8f)
                curveTo(7.5f, 3.8f, 4f, 7.5f, 4f, 12f)
                lineTo(4f, 13f)
                // Bottom arc
                moveTo(4f, 13.5f)
                curveTo(4.8f, 17.3f, 8.1f, 20.2f, 12f, 20.2f)
                curveTo(16.5f, 20.2f, 20f, 16.5f, 20f, 12f)
                lineTo(20f, 11f)
            }
            // Arrowhead 1
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.5f, 10.5f)
                lineTo(20f, 10.5f)
                lineTo(20f, 7f)
            }
            // Arrowhead 2
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7.5f, 13.5f)
                lineTo(4f, 13.5f)
                lineTo(4f, 17f)
            }
        }.build()
    }

    /**
     * Modern Minimalist Trash
     */
    val Trash: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernTrash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Bin body
                moveTo(5.5f, 7.5f)
                lineTo(6.8f, 19.2f)
                curveTo(6.9f, 20.2f, 7.8f, 21f, 8.8f, 21f)
                lineTo(15.2f, 21f)
                curveTo(16.2f, 21f, 17.1f, 20.2f, 17.2f, 19.2f)
                lineTo(18.5f, 7.5f)
                // Lid
                moveTo(3.5f, 7.5f)
                lineTo(20.5f, 7.5f)
                // Handle
                moveTo(9.5f, 7.5f)
                lineTo(9.5f, 4.5f)
                lineTo(14.5f, 4.5f)
                lineTo(14.5f, 7.5f)
            }
        }.build()
    }

    /**
     * Modern Share / Network Nodes
     */
    val Share: ImageVector by lazy {
        ImageVector.Builder(
            name = "ModernShare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8.5f, 11f); lineTo(15.5f, 7f)
                moveTo(8.5f, 13f); lineTo(15.5f, 17f)
            }
            // 3 circular nodes
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 12f)
                curveTo(7.1f, 12f, 8f, 11.1f, 8f, 10f)
                curveTo(8f, 8.9f, 7.1f, 8f, 6f, 8f)
                curveTo(4.9f, 8f, 4f, 8.9f, 4f, 10f)
                curveTo(4f, 11.1f, 4.9f, 12f, 6f, 12f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 7f)
                curveTo(19.1f, 7f, 20f, 6.1f, 20f, 5f)
                curveTo(20f, 3.9f, 19.1f, 3f, 18f, 3f)
                curveTo(16.9f, 3f, 16f, 3.9f, 16f, 5f)
                curveTo(16f, 6.1f, 16.9f, 7f, 18f, 7f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 21f)
                curveTo(19.1f, 21f, 20f, 20.1f, 20f, 19f)
                curveTo(20f, 17.9f, 19.1f, 17f, 18f, 17f)
                curveTo(16.9f, 17f, 16f, 17.9f, 16f, 19f)
                curveTo(16f, 20.1f, 16.9f, 21f, 18f, 21f)
                close()
            }
        }.build()
    }
}
