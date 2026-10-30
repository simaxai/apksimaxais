package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.model.ModelTier
import com.example.model.SpecialistPersona
import com.example.ui.components.CyberGridBackground
import com.example.ui.components.GlitchText
import com.example.ui.components.MessageItem
import com.example.ui.components.ModernIcons
import com.example.ui.components.SimaxLogo
import com.example.ui.dialogs.GoogleAuthDialog
import com.example.ui.dialogs.LiveVoiceDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.UpgradeDialog
import com.example.ui.drawer.SessionsDrawer
import com.example.ui.drawer.getModelIcon
import com.example.ui.drawer.getPersonaIcon
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500
import com.example.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun MainChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val sessions by viewModel.sessions.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val isReasoningActive by viewModel.isReasoningActive.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val attachedBitmap by viewModel.attachedBitmap.collectAsState()
    val attachedName by viewModel.attachedName.collectAsState()
    val attachedDocName by viewModel.attachedDocName.collectAsState()

    val showUpgradeDialog by viewModel.showUpgradeDialog.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()
    val showGoogleAuthDialog by viewModel.showGoogleAuthDialog.collectAsState()
    val isGoogleLoggedIn by viewModel.isGoogleLoggedIn.collectAsState()
    val googleEmail by viewModel.googleEmail.collectAsState()

    var modelMenuExpanded by remember { mutableStateOf(false) }
    var personaMenuExpanded by remember { mutableStateOf(false) }
    var attachMenuExpanded by remember { mutableStateOf(false) }

    // Scroll to bottom automatically on new messages, streaming, and responses
    val lastMessageStreaming = messages.lastOrNull()?.streaming
    LaunchedEffect(messages.size, isBusy, lastMessageStreaming) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Scroll-to-bottom FAB visibility
    val showScrollToBottom by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 4 && lastVisibleIndex < totalItems - 2
        }
    }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = it.lastPathSegment?.substringAfterLast('/') ?: "image.jpg"
            viewModel.setAttachedImage(it, name)
        }
    }

    // Universal Document & Data File Picker Launcher (PDF, CSV, TXT, JSON, code, docs)
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadAndAttachAnyFile(it) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SessionsDrawer(
                sessions = sessions,
                activeSessionId = activeSessionId,
                onSelectSession = { viewModel.selectSession(it) },
                onNewChat = { viewModel.newChat() },
                onDeleteSession = { viewModel.deleteSession(it) },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                activePersona = activePersona,
                onSelectPersona = { viewModel.selectPersona(it) },
                isGoogleLoggedIn = isGoogleLoggedIn,
                googleEmail = googleEmail,
                onOpenGoogleAuth = { viewModel.openGoogleAuthDialog() }
            )
        }
    ) {
        // CRITICAL INSET FIX: Do NOT apply imePadding or statusBarsPadding to outer Scaffold.
        // Doing so crushes the top navigation bar when keyboard opens (the screenshot bug).
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                // Top Navigation Bar - strictly pinned below status bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Drawer menu & Logo & Model Selector & Persona
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(3.dp))

                            SimaxLogo(size = 28.dp, hasGlow = true)

                            Spacer(modifier = Modifier.width(7.dp))

                            // Model Selector Dropdown
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, SimaxBlood.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                        .clickable { modelMenuExpanded = true }
                                        .padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getModelIcon(selectedModel),
                                        contentDescription = null,
                                        tint = if (selectedModel.isPro) SimaxEmber else SimaxBlood,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = selectedModel.label,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = SimaxTextZinc500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = modelMenuExpanded,
                                    onDismissRequest = { modelMenuExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                ) {
                                    ModelTier.entries.forEach { model ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = getModelIcon(model),
                                                        contentDescription = null,
                                                        tint = if (model.isPro) SimaxEmber else SimaxBlood,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = model.label,
                                                            color = if (model == selectedModel) SimaxBlood2 else MaterialTheme.colorScheme.onSurface,
                                                            fontSize = 12.sp,
                                                            fontFamily = JetBrainsMonoFontFamily,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = model.desc,
                                                            color = SimaxTextZinc500,
                                                            fontSize = 10.sp,
                                                            fontFamily = InterFontFamily
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                modelMenuExpanded = false
                                                viewModel.selectModel(model)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Specialist Persona Pill (Custom Vector Icons, zero emojis)
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                                        .clickable { personaMenuExpanded = true }
                                        .padding(horizontal = 7.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getPersonaIcon(activePersona),
                                        contentDescription = null,
                                        tint = SimaxBlood2,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = activePersona.title.take(12),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        fontFamily = InterFontFamily
                                    )
                                }

                                DropdownMenu(
                                    expanded = personaMenuExpanded,
                                    onDismissRequest = { personaMenuExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                ) {
                                    SpecialistPersona.entries.forEach { persona ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = getPersonaIcon(persona),
                                                        contentDescription = null,
                                                        tint = if (persona == activePersona) SimaxBlood2 else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = persona.title,
                                                            color = if (persona == activePersona) SimaxBlood2 else MaterialTheme.colorScheme.onSurface,
                                                            fontSize = 12.sp,
                                                            fontFamily = InterFontFamily,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = persona.description,
                                                            color = SimaxTextZinc500,
                                                            fontSize = 10.sp,
                                                            fontFamily = InterFontFamily
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                personaMenuExpanded = false
                                                viewModel.selectPersona(persona)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Right: Actions (Upgrade pill, Voice, Theme, Settings, New)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pro / Upgrade button
                            if (isSubscribed) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SimaxEmber.copy(alpha = 0.2f))
                                        .border(1.dp, SimaxEmber, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = SimaxEmber,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = "UPGRADE",
                                    color = SimaxEmber,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, SimaxEmber.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                        .clickable { viewModel.openUpgradeDialog() }
                                        .padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }

                            // Voice Mode Button
                            IconButton(
                                onClick = { viewModel.openVoiceDialog() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.VoiceWave,
                                    contentDescription = "Live Voice",
                                    tint = SimaxBlood2,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            // Theme Toggle
                            IconButton(
                                onClick = { viewModel.toggleTheme() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) ModernIcons.Sun else ModernIcons.Moon,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Google Account / Sign-In
                            IconButton(
                                onClick = { viewModel.openGoogleAuthDialog() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Google,
                                    contentDescription = "Google Account",
                                    tint = if (isGoogleLoggedIn) Color(0xFF4285F4) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Settings
                            IconButton(
                                onClick = { viewModel.openSettingsDialog() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            // New Chat Button
                            IconButton(
                                onClick = { viewModel.newChat() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Add,
                                    contentDescription = "New Chat",
                                    tint = SimaxBlood2,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Cyber grid decoration background
                CyberGridBackground(isDark = isDarkTheme)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat area / Empty state
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (messages.isEmpty()) {
                            // Hero empty state with scrollable container to prevent crushing when keyboard opens
                            EmptyStateHero(
                                selectedModel = selectedModel,
                                activePersona = activePersona,
                                onPromptSelected = { prompt ->
                                    keyboardController?.hide()
                                    viewModel.sendMessage(prompt)
                                }
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(messages, key = { it.id }) { message ->
                                    MessageItem(
                                        message = message,
                                        onRegenerate = { viewModel.regenerateMessage(message.id) },
                                        onSpeak = { viewModel.voiceManager.speak(it) }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }

                        // Scroll to bottom FAB
                        if (showScrollToBottom) {
                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            listState.animateScrollToItem(messages.size - 1)
                                        }
                                    }
                                },
                                containerColor = SimaxBlood,
                                contentColor = Color.Black,
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Scroll to bottom",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Bottom Input Area - handles keyboard insets cleanly
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        // Top toolbar: Deep Think pill & attached media
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Deep Reasoning Mode (ChatGPT o1 style)
                            val thinkBorderColor by animateColorAsState(
                                if (isReasoningActive) SimaxBlood else MaterialTheme.colorScheme.outlineVariant,
                                animationSpec = tween(250)
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isReasoningActive) SimaxBlood.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, thinkBorderColor, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.toggleReasoning() }
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Brain,
                                    contentDescription = "Deep Reasoning",
                                    tint = if (isReasoningActive) SimaxBlood else SimaxTextZinc500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isReasoningActive) "تفكير عميق: مفعل" else "تفكير عميق (Deep Think)",
                                    color = if (isReasoningActive) SimaxBlood2 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isReasoningActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            // Attached file pill if active
                            if (attachedBitmap != null || attachedDocName != null) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, SimaxBlood.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (attachedBitmap != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = attachedBitmap),
                                            contentDescription = "Attached",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = attachedName ?: "Image attached",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else {
                                        Icon(
                                            imageVector = ModernIcons.Document,
                                            contentDescription = null,
                                            tint = SimaxBlood2,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = attachedDocName ?: "Data file",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = SimaxBlood2,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.clearAttachment() }
                                    )
                                }
                            }
                        }

                        // Oval input bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(26.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(26.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // File attachment button with options menu
                            Box {
                                IconButton(
                                    onClick = { attachMenuExpanded = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = ModernIcons.Paperclip,
                                        contentDescription = "Attach media",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = attachMenuExpanded,
                                    onDismissRequest = { attachMenuExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = ModernIcons.Photo,
                                                    contentDescription = null,
                                                    tint = SimaxBlood2,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "تحميل صورة (Vision)",
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 12.sp,
                                                    fontFamily = InterFontFamily
                                                )
                                            }
                                        },
                                        onClick = {
                                            attachMenuExpanded = false
                                            imagePickerLauncher.launch("image/*")
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = ModernIcons.Document,
                                                    contentDescription = null,
                                                    tint = SimaxBlood2,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "قراءة ملف أو مستند (PDF / CSV / Docs)",
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 12.sp,
                                                    fontFamily = InterFontFamily
                                                )
                                            }
                                        },
                                        onClick = {
                                            attachMenuExpanded = false
                                            docPickerLauncher.launch("*/*")
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = ModernIcons.Palette,
                                                    contentDescription = null,
                                                    tint = SimaxBlood2,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "توليد صورة بالذكاء الاصطناعي (Forge)",
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 12.sp,
                                                    fontFamily = InterFontFamily
                                                )
                                            }
                                        },
                                        onClick = {
                                            attachMenuExpanded = false
                                            viewModel.onInputTextChanged("ارسم لي ")
                                        }
                                    )
                                }
                            }

                            // Text field
                            TextField(
                                value = inputText,
                                onValueChange = { viewModel.onInputTextChanged(it) },
                                placeholder = {
                                    Text(
                                        text = "اسأل ${selectedModel.label}...",
                                        color = SimaxTextZinc500,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    cursorColor = SimaxBlood
                                ),
                                maxLines = 4,
                                modifier = Modifier.weight(1f)
                            )

                            // Voice quick mic button
                            IconButton(
                                onClick = { viewModel.openVoiceDialog() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = ModernIcons.Mic,
                                    contentDescription = "Voice input",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Send or Stop button
                            val hasInput = inputText.isNotBlank() || attachedBitmap != null || attachedDocName != null
                            if (isBusy) {
                                IconButton(
                                    onClick = { viewModel.stopGeneration() },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SimaxBlood)
                                ) {
                                    Icon(
                                        imageVector = ModernIcons.Stop,
                                        contentDescription = "Stop",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        viewModel.sendMessage()
                                    },
                                    enabled = hasInput,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (hasInput) SimaxBlood else MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(
                                        imageVector = ModernIcons.Send,
                                        contentDescription = "Send",
                                        tint = if (hasInput) Color.Black else SimaxTextZinc500,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        // Footer disclaimer
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "قد يظهر SIMAX AI بعض التقديرات غير الدقيقة. يرجى مراجعة وتدقيق الحقائق الهامة.",
                            color = SimaxTextZinc500,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Upgrade Dialog
    if (showUpgradeDialog) {
        UpgradeDialog(
            onDismiss = { viewModel.closeUpgradeDialog() },
            onActivateKey = { key -> viewModel.activateLicenseKey(key) }
        )
    }

    // Settings Dialog with Custom Instructions & Memory
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { viewModel.closeSettingsDialog() },
            isDarkTheme = isDarkTheme,
            onThemeToggle = { viewModel.setTheme(it) },
            isSubscribed = isSubscribed,
            onActivateKey = { key -> viewModel.activateLicenseKey(key) },
            onDeactivateSubscription = { viewModel.deactivateSubscription() },
            onClearConversations = { viewModel.clearAllConversations() },
            onClearMemory = { viewModel.clearMemory() },
            onExportData = { /* Handled in VM */ },
            onOpenUpgrade = {
                viewModel.closeSettingsDialog()
                viewModel.openUpgradeDialog()
            },
            userMemory = viewModel.getUserMemory(),
            onSaveCustomInstructions = { about, resp -> viewModel.saveCustomInstructions(about, resp) },
            onRemoveFact = { viewModel.removeUserFact(it) }
        )
    }

    // Voice Dialog
    if (showVoiceDialog) {
        LiveVoiceDialog(
            voiceManager = viewModel.voiceManager,
            selectedModel = selectedModel,
            onDismiss = { viewModel.closeVoiceDialog() }
        )
    }

    // Google Sign-In Dialog
    if (showGoogleAuthDialog) {
        GoogleAuthDialog(
            currentEmail = googleEmail,
            isLoggedIn = isGoogleLoggedIn,
            onSignIn = { email -> viewModel.signInWithGoogle(email) },
            onSignOut = { viewModel.signOutGoogle() },
            onDismiss = { viewModel.closeGoogleAuthDialog() }
        )
    }
}

@Composable
fun EmptyStateHero(
    selectedModel: ModelTier,
    activePersona: SpecialistPersona,
    onPromptSelected: (String) -> Unit
) {
    val quotes = remember {
        listOf(
            "High Precision Intelligence",
            "Welcome to the future",
            "The depth holds everything",
            "Your signal, my precision",
            "Ready when you are",
            "Think big, ask anything",
            "Always sharp",
            "One question away"
        )
    }
    val quote = remember { quotes.random() }

    val promptSuggestions = remember {
        listOf(
            "ارسم تصميماً مستقبلياً فائق الدقة بأسلوب فني سينمائي",
            "اكتب كود بايثون متقدم مع أفضل ممارسات هندسة البرمجيات",
            "ما هي أحدث تطورات العلوم والتكنولوجيا العالمية الموثقة اليوم؟",
            "اشرح حل المعادلات الرياضية المعقدة بأسلوب تحليلي دقيق"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SimaxLogo(size = 64.dp, hasGlow = true)

        Spacer(modifier = Modifier.height(14.dp))

        GlitchText(
            text = quote,
            style = androidx.compose.ui.text.TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "What would you like to explore today? / ما الذي ترغب في استكشافه اليوم؟",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontFamily = InterFontFamily,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Tier Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SimaxBlood.copy(alpha = 0.12f))
                    .border(1.dp, SimaxBlood.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getModelIcon(selectedModel),
                        contentDescription = null,
                        tint = if (selectedModel.isPro) SimaxEmber else SimaxBlood,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedModel.label,
                        color = SimaxBlood2,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Persona Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getPersonaIcon(activePersona),
                        contentDescription = null,
                        tint = SimaxBlood2,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = activePersona.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick suggested prompt pills (clean vector icon accents, zero emojis)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (p in promptSuggestions) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable { onPromptSelected(p) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ModernIcons.Sparkle,
                            contentDescription = null,
                            tint = SimaxBlood2,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = p,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }
    }
}
