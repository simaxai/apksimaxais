package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxLine
import com.example.ui.theme.SimaxPanel
import com.example.ui.theme.SimaxPanel2
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextWhite
import com.example.ui.theme.SimaxTextZinc300
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessageItem(
    message: ChatMessage,
    onRegenerate: () -> Unit,
    onSpeak: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val isUser = message.role == MessageRole.USER
    var copied by remember { mutableStateOf(false) }
    var selectedFullscreenImage by remember { mutableStateOf<String?>(null) }

    // Typewriter terminal animation effect for AI messages
    var displayedLength by remember(message.id) {
        mutableStateOf(if (message.streaming) message.text.length else 0)
    }

    LaunchedEffect(message.text, message.streaming) {
        if (isUser || message.streaming) {
            displayedLength = message.text.length
        } else if (displayedLength < message.text.length) {
            while (displayedLength < message.text.length) {
                val step = kotlin.math.max(1, (message.text.length - displayedLength) / 6 + 3)
                displayedLength = kotlin.math.min(message.text.length, displayedLength + step)
                delay(12L)
            }
        }
    }

    val cleanedRawText = remember(message.text) {
        message.text.trim()
            .removePrefix("You: ")
            .removePrefix("you: ")
            .removePrefix("You:")
            .removePrefix("Simax AI: ")
            .removePrefix("SIMAX AI: ")
            .removePrefix("simax ai: ")
            .removePrefix("SIMAX: ")
            .removePrefix("Simax:")
            .removePrefix("AI: ")
            .trim()
    }

    val isTyping = !isUser && (message.streaming || displayedLength < cleanedRawText.length)
    val textToRender = if (isUser) cleanedRawText else cleanedRawText.take(displayedLength)

    if (isUser) {
        // User message bubble (Clean, modern right-aligned card without avatar)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    if (message.mediaDataUri != null) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = message.mediaDataUri),
                                contentDescription = "Attached media",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }

                    Text(
                        text = textToRender,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = com.example.ui.theme.InterFontFamily
                    )
                }
            }
        }
    } else {
        // AI message container (Expansive, clean full-width layout without avatar, like Gemini & ChatGPT)
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            // Web search indicator
            if (message.isSearching) {
                WebSearchIndicator()
            }

                    // Deep Reasoning Accordion (ChatGPT o1 / Gemini Thinking style)
                    if (message.thinkingText != null) {
                        var thinkingExpanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, SimaxBlood.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { thinkingExpanded = !thinkingExpanded }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = ModernIcons.Brain,
                                        contentDescription = null,
                                        tint = SimaxBlood2,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (message.thinkingDurationSeconds != null)
                                            "مسار التفكير المنطقي (${message.thinkingDurationSeconds} ث)"
                                        else "مسار التفكير المنطقي",
                                        color = SimaxBlood2,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (thinkingExpanded) "▲ إخفاء" else "▼ تفاصيل",
                                    color = SimaxTextZinc500,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            androidx.compose.animation.AnimatedVisibility(visible = thinkingExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(SimaxBlood.copy(alpha = 0.2f))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = message.thinkingText,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Image generation forge or result
                    if (message.imagePrompt != null) {
                        if (message.streaming || message.imageUrls.isEmpty()) {
                            ImageForgeView(prompt = message.imagePrompt)
                        } else {
                            GeneratedImageCard(
                                prompt = message.imagePrompt,
                                imageUrls = message.imageUrls,
                                onRetry = onRegenerate,
                                onImageClick = { selectedFullscreenImage = it }
                            )
                        }
                    } else {
                        if (message.streaming && message.text.isEmpty() && !message.isSearching) {
                            ThinkingIndicator()
                        } else {
                            // High-fidelity Markdown with Typewriter Terminal Stream & Code Block separation!
                            val contentWithCursor = if (isTyping && textToRender.isNotEmpty()) "$textToRender ▌" else textToRender
                            MarkdownContent(text = contentWithCursor)
                        }
                    }

                    // Verified Web Search Sources Cards
                    if (message.searchSources.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = "المصادر الموثقة (Verified Sources):",
                                color = SimaxTextZinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                message.searchSources.forEach { source ->
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                            .clickable {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {}
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = ModernIcons.Search,
                                            contentDescription = null,
                                            tint = SimaxBlood2,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = source.title.take(24),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

            // Bottom Actions (Copy, TTS, Share, Regenerate)
            if (!isUser && !message.streaming && (message.text.isNotEmpty() || message.imagePrompt != null)) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Message
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val textToCopy = message.imagePrompt ?: message.text
                            copyToClipboard(context, textToCopy)
                            copied = true
                            scope.launch {
                                delay(1400L)
                                copied = false
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (copied) ModernIcons.Check else ModernIcons.Copy,
                            contentDescription = "Copy message",
                            tint = if (copied) SimaxBlood2 else SimaxTextZinc500,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Speak aloud (TTS)
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSpeak(message.text)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = ModernIcons.VoiceWave,
                            contentDescription = "Listen to message",
                            tint = SimaxTextZinc500,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "SIMAX AI Response")
                                putExtra(Intent.EXTRA_TEXT, message.text)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = ModernIcons.Share,
                            contentDescription = "Share",
                            tint = SimaxTextZinc500,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Regenerate
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onRegenerate()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = ModernIcons.Refresh,
                            contentDescription = "Regenerate",
                            tint = SimaxTextZinc500,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Image Dialog
    selectedFullscreenImage?.let { url ->
        FullScreenImageViewer(
            imageUrl = url,
            prompt = message.imagePrompt ?: "SIMAX AI",
            onDismiss = { selectedFullscreenImage = null }
        )
    }
}

@Composable
fun GeneratedImageCard(
    prompt: String,
    imageUrls: List<String>,
    onRetry: () -> Unit,
    onImageClick: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }
    val currentUrl = imageUrls.getOrNull(selectedIndex) ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SimaxPanel)
            .border(1.dp, SimaxBlood.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .clickable { onImageClick(currentUrl) },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentUrl,
                contentDescription = prompt,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            // Zoom hint badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "TAP TO EXPAND",
                    color = SimaxBlood2,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\"$prompt\"",
                color = SimaxTextZinc400,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "COPY",
                    color = SimaxBlood2,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            copyToClipboard(context, prompt)
                            Toast.makeText(context, "Prompt copied", Toast.LENGTH_SHORT).show()
                        }
                        .padding(4.dp)
                )

                if (imageUrls.size > 1) {
                    Text(
                        text = "NEXT (${selectedIndex + 1}/${imageUrls.size})",
                        color = SimaxSignal,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                selectedIndex = (selectedIndex + 1) % imageUrls.size
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("SIMAX", text)
    clipboard.setPrimaryClip(clip)
}
