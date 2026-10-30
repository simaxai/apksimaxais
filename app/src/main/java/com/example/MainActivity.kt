package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainChatScreen
import com.example.ui.components.VideoSplashScreen
import com.example.ui.theme.SimaxTheme
import com.example.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.security.SimaxSecurityGuard.init(this)
        enableEdgeToEdge()
        setContent {
            val viewModel: ChatViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            var isSplashVisible by remember { mutableStateOf(true) }

            // Request microphone permission for voice chat
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Permission result handled
            }

            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }

            SimaxTheme(darkTheme = isDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSplashVisible) Color.Black else Color.Unspecified)
                ) {
                    MainChatScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )

                    AnimatedVisibility(
                        visible = isSplashVisible,
                        exit = fadeOut(animationSpec = tween(500)),
                        enter = fadeIn()
                    ) {
                        VideoSplashScreen(
                            onFinished = { isSplashVisible = false },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
