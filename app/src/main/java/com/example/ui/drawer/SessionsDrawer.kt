package com.example.ui.drawer

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatSession
import com.example.model.SpecialistPersona
import com.example.ui.components.ModernIcons
import com.example.ui.components.SimaxLogo
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxLine
import com.example.ui.theme.SimaxPanel
import com.example.ui.theme.SimaxPanel2
import com.example.ui.theme.SimaxTextWhite
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500

@Composable
fun SessionsDrawer(
    sessions: List<ChatSession>,
    activeSessionId: String?,
    onSelectSession: (String) -> Unit,
    onNewChat: () -> Unit,
    onDeleteSession: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    activePersona: SpecialistPersona = SpecialistPersona.GENERAL,
    onSelectPersona: (SpecialistPersona) -> Unit = {},
    isGoogleLoggedIn: Boolean = true,
    googleEmail: String = "simaxx991top@gmail.com",
    onOpenGoogleAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(295.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(14.dp)
    ) {
        // "+ New chat" button
        Button(
            onClick = {
                onNewChat()
                onCloseDrawer()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SimaxBlood.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+ New chat",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = ModernIcons.Add,
                    contentDescription = null,
                    tint = SimaxBlood2,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Custom Specialist Personas section (100% Vector icons, zero emojis)
        Text(
            text = "SPECIALIST PERSONAS (المساعدون)",
            color = SimaxTextZinc500,
            fontSize = 9.sp,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SpecialistPersona.entries.forEach { persona ->
                val isSelected = persona == activePersona
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SimaxBlood.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            1.dp,
                            if (isSelected) SimaxBlood2 else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onSelectPersona(persona)
                            onCloseDrawer()
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getPersonaIcon(persona),
                            contentDescription = null,
                            tint = if (isSelected) SimaxBlood2 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = persona.title,
                            color = if (isSelected) SimaxBlood2 else MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            fontFamily = InterFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "RECENT SESSIONS",
            color = SimaxTextZinc500,
            fontSize = 9.sp,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        // Sessions list
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sessions yet.",
                    color = SimaxTextZinc500,
                    fontSize = 11.sp,
                    fontFamily = InterFontFamily
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    val isActive = session.id == activeSessionId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) SimaxBlood.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                onSelectSession(session.id)
                                onCloseDrawer()
                            }
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.title,
                            color = if (isActive) SimaxBlood2 else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontFamily = InterFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { onDeleteSession(session.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = ModernIcons.Trash,
                                contentDescription = "Delete session",
                                tint = SimaxTextZinc500,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Google Account Section (Authentic Google Sign-In state like web)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onOpenGoogleAuth() }
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ModernIcons.Google,
                            contentDescription = "Google",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = if (isGoogleLoggedIn) "Google Account" else "Sign In with Google",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isGoogleLoggedIn) googleEmail else "اضغط لتسجيل الدخول",
                            color = if (isGoogleLoggedIn) SimaxBlood2 else SimaxTextZinc500,
                            fontSize = 9.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isGoogleLoggedIn) Color(0xFF34A853) else SimaxBlood)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Footer: SIMAX TEAM + Official Emblem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SimaxLogo(size = 30.dp, hasGlow = true)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SIMAX",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "High Precision Intelligence",
                        color = SimaxTextZinc500,
                        fontSize = 9.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }
    }
}

fun getPersonaIcon(persona: SpecialistPersona): ImageVector {
    return when (persona) {
        SpecialistPersona.GENERAL -> ModernIcons.GeneralPersona
        SpecialistPersona.REASONING -> ModernIcons.Brain
        SpecialistPersona.CODER -> ModernIcons.Code
        SpecialistPersona.DATA_ANALYST -> ModernIcons.Chart
        SpecialistPersona.WEB_RESEARCHER -> ModernIcons.Globe
        SpecialistPersona.CREATIVE_WRITER -> ModernIcons.Pen
        SpecialistPersona.CYBER_SEC -> ModernIcons.Shield
    }
}

fun getModelIcon(model: com.example.model.ModelTier): ImageVector {
    return when (model) {
        com.example.model.ModelTier.S_TURBO_FLASH -> ModernIcons.FlashTier
        com.example.model.ModelTier.S_TURBO_HIGH -> ModernIcons.HighTier
        com.example.model.ModelTier.S_TURBO_MAX -> ModernIcons.MaxTier
        com.example.model.ModelTier.S_TURBO_ULTRA_MAX -> ModernIcons.UltraMaxTier
    }
}
