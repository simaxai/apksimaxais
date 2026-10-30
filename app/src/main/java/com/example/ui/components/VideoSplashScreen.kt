package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.ui.theme.SimaxBlood2
import kotlinx.coroutines.delay

@Composable
fun VideoSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isVideoFinished by remember { mutableStateOf(false) }

    fun finishSplash() {
        if (!isVideoFinished) {
            isVideoFinished = true
            onFinished()
        }
    }

    // Safety timeout fallback (3.8 seconds)
    LaunchedEffect(Unit) {
        delay(3800L)
        finishSplash()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { finishSplash() }
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = Uri.parse("android.resource://${ctx.packageName}/${R.raw.intro_splash}")
                    setVideoURI(uri)
                    setBackgroundColor(android.graphics.Color.BLACK)
                    setOnPreparedListener { mp ->
                        mp.isLooping = false
                        mp.setVolume(0f, 0f) // Muted without sound as requested
                        start()
                    }
                    setOnCompletionListener {
                        finishSplash()
                    }
                    setOnErrorListener { _, _, _ ->
                        finishSplash()
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Subtle skip button at top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 44.dp, end = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { finishSplash() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "تخطي / SKIP",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
