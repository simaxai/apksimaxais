package com.example.ui.dialogs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ModelTier
import com.example.ui.components.SimaxLogo
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxCrimson
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxLine
import com.example.ui.theme.SimaxPanel
import com.example.ui.theme.SimaxPanel2
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextWhite
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500
import com.example.voice.VoiceManager
import com.example.voice.VoiceState

@Composable
fun LiveVoiceDialog(
    voiceManager: VoiceManager,
    selectedModel: ModelTier,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val voiceState by voiceManager.voiceState.collectAsState()
    val partialText by voiceManager.partialText.collectAsState()

    val transition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    val orbBorderColor = when (voiceState) {
        VoiceState.IDLE -> SimaxLine
        VoiceState.LISTENING -> SimaxBlood
        VoiceState.THINKING -> SimaxEmber
        VoiceState.SPEAKING -> SimaxSignal
    }

    val orbGlowColor = when (voiceState) {
        VoiceState.IDLE -> Color.Transparent
        VoiceState.LISTENING -> SimaxBlood.copy(alpha = 0.35f)
        VoiceState.THINKING -> SimaxEmber.copy(alpha = 0.35f)
        VoiceState.SPEAKING -> SimaxSignal.copy(alpha = 0.35f)
    }

    val statusLabel = when (voiceState) {
        VoiceState.IDLE -> "Tap orb to talk"
        VoiceState.LISTENING -> "Listening..."
        VoiceState.THINKING -> "Thinking..."
        VoiceState.SPEAKING -> "Speaking..."
    }

    Dialog(onDismissRequest = {
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        onDismiss()
    }) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (voiceState != VoiceState.IDLE) SimaxBlood else SimaxTextZinc500)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Voice · ${selectedModel.label}",
                            color = SimaxTextZinc400,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            voiceManager.stopSpeaking()
                            voiceManager.stopListening()
                            onDismiss()
                        },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = com.example.ui.components.ModernIcons.Add,
                            contentDescription = "Close",
                            tint = SimaxTextZinc500,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Pulsing Voice Orb Button
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(if (voiceState != VoiceState.IDLE) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    orbGlowColor,
                                    SimaxPanel2,
                                    Color(0xFF040404)
                                )
                            )
                        )
                        .border(2.dp, orbBorderColor, CircleShape)
                        .clickable {
                            if (voiceState == VoiceState.LISTENING) {
                                voiceManager.stopListening()
                            } else if (voiceState == VoiceState.SPEAKING) {
                                voiceManager.stopSpeaking()
                            } else {
                                voiceManager.startListening()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    SimaxLogo(size = 80.dp, hasGlow = voiceState != VoiceState.IDLE)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Status text
                Text(
                    text = statusLabel.uppercase(),
                    color = SimaxBlood2,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Partial text speech transcription
                Text(
                    text = partialText.ifBlank { "Speak naturally, SIMAX will answer with voice." },
                    color = SimaxTextZinc400,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Tap the orb to start or pause voice conversation.",
                    color = SimaxTextZinc500,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
