package com.example.ui.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.UserMemory
import com.example.ui.components.ModernIcons
import com.example.ui.components.SimaxLogo
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500

enum class SettingsTab(val label: String, val icon: ImageVector) {
    GENERAL("General", ModernIcons.Settings),
    INSTRUCTIONS("Memory & Instructions", ModernIcons.Brain),
    RESPONSES("Models & Intelligence", ModernIcons.Sparkle),
    SUBSCRIPTION("Subscription", ModernIcons.Check),
    DATA("Data & Privacy", ModernIcons.Trash),
    ABOUT("About", ModernIcons.Document)
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    isSubscribed: Boolean,
    onActivateKey: (String) -> Boolean,
    onDeactivateSubscription: () -> Unit,
    onClearConversations: () -> Unit,
    onClearMemory: () -> Unit,
    onExportData: () -> Unit,
    onOpenUpgrade: () -> Unit,
    userMemory: UserMemory = UserMemory(),
    onSaveCustomInstructions: (String?, String?) -> Unit = { _, _ -> },
    onRemoveFact: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(SettingsTab.GENERAL) }
    var inputKey by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

    // Custom instructions fields
    var aboutUserText by remember(userMemory) { mutableStateOf(userMemory.aboutUser ?: "") }
    var responseInstructionsText by remember(userMemory) { mutableStateOf(userMemory.responseInstructions ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Sidebar Navigation
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SimaxLogo(size = 22.dp, hasGlow = false)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settings",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsTab.entries.forEach { tab ->
                        val isSelected = tab == selectedTab
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SimaxBlood.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 8.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) SimaxBlood2 else SimaxTextZinc500,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tab.label,
                                color = if (isSelected) SimaxBlood2 else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "SIMAX AI v3.0",
                        color = SimaxTextZinc500,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                // Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(14.dp)
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTab.label,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = ModernIcons.Add,
                                contentDescription = "Close",
                                tint = SimaxTextZinc500,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (selectedTab) {
                            SettingsTab.GENERAL -> {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    // Theme switch
                                    Text(
                                        text = "Theme (المظهر)",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                            .padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isDarkTheme) SimaxBlood else Color.Transparent)
                                                .clickable { onThemeToggle(true) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = ModernIcons.Moon,
                                                    contentDescription = null,
                                                    tint = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Night (ليلي)",
                                                    color = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (!isDarkTheme) SimaxBlood else Color.Transparent)
                                                .clickable { onThemeToggle(false) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = ModernIcons.Sun,
                                                    contentDescription = null,
                                                    tint = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Solar (نهاري)",
                                                    color = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Response Language (لغة الرد)",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "الذكاء الاصطناعي يتعرف على لغتك فوراً وبشكل تلقائي (عربية، دارجة جزائرية، إنجليزية، فرنسية، وغيرها).",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            SettingsTab.INSTRUCTIONS -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "Custom Instructions (التعليمات المخصصة)",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "اضبط إعدادات مسبقة تحدد هويتك وتفضيلاتك في الرد ليتذكرها الذكاء الاصطناعي دائماً.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )

                                    Text(
                                        text = "ما الذي ترغب أن يعرفه الذكاء الاصطناعي عنك؟",
                                        color = SimaxBlood2,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedTextField(
                                        value = aboutUserText,
                                        onValueChange = { aboutUserText = it },
                                        placeholder = {
                                            Text(
                                                "مثال: أنا مهندس برمجيات مقيم في الجزائر، أعمل على مشاريع Kotlin و Python...",
                                                fontSize = 10.sp,
                                                color = SimaxTextZinc500
                                            )
                                        },
                                        maxLines = 3,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SimaxBlood,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    Text(
                                        text = "كيف تفضل أن يصيغ الذكاء الاصطناعي إجاباته؟",
                                        color = SimaxBlood2,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedTextField(
                                        value = responseInstructionsText,
                                        onValueChange = { responseInstructionsText = it },
                                        placeholder = {
                                            Text(
                                                "مثال: أجب مباشرة دون اعتذارات أو مقدمات، واكتب أكواداً مكتملة ونظيفة...",
                                                fontSize = 10.sp,
                                                color = SimaxTextZinc500
                                            )
                                        },
                                        maxLines = 3,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SimaxBlood,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            onSaveCustomInstructions(aboutUserText, responseInstructionsText)
                                            Toast.makeText(context, "تم حفظ التعليمات المخصصة بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SimaxBlood),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("حفظ التعليمات (Save)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    // Active Remembered facts
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "الذاكرة المستمرة (Active Learned Memory):",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (userMemory.facts.isEmpty()) {
                                        Text(
                                            text = "لم يتم حفظ حقائق تلقائية بعد. سيحفظ الذكاء الاصطناعي معلوماتك عند قول 'تذكر أن...'.",
                                            color = SimaxTextZinc500,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            userMemory.facts.forEach { fact ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "• $fact",
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            onRemoveFact(fact)
                                                            Toast.makeText(context, "تم حذف المعلومة", Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = ModernIcons.Trash,
                                                            contentDescription = "Delete fact",
                                                            tint = SimaxBlood2,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            SettingsTab.RESPONSES -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "عائلة نماذج S TURBO الفائقة:",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )

                                    val tiers = listOf(
                                        Triple("S TURBO FLASH", "الاستجابة الفائقة — السرعة القصوى مع فهم شامل وسياق عميق", SimaxBlood),
                                        Triple("S TURBO HIGH", "النموذج المتوازن — تحليل مفصل وأسلوب لغوي رفيع", SimaxBlood2),
                                        Triple("S TURBO MAX", "الاستدلال والتفكير العميق — حل أعقد المسائل والبرمجيات", SimaxEmber),
                                        Triple("S TURBO ULTRA MAX", "المستوى النخبوي الخارق — تاريخ الأرض والحضارات وتوليد صور غير محدود", SimaxSignal)
                                    )

                                    tiers.forEach { (name, desc, col) ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(col)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(text = name, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text(text = desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            SettingsTab.SUBSCRIPTION -> {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, if (isSubscribed) SimaxEmber.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isSubscribed) "SIMAX PRO Active (مفعل)" else "S TURBO ULTRA MAX",
                                                color = if (isSubscribed) SimaxEmber else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isSubscribed)
                                                    "أنت تتمتع بوصول كامل إلى أعتى نماذج الذكاء الاصطناعي مع توليد صور غير محدود وتفكير استدلالي عميق."
                                                else
                                                    "افتح الوصول إلى النموذج النخبوي وتوليد الصور اللانهائي عبر إدخال مفتاح التفعيل.",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }

                                    if (!isSubscribed) {
                                        Button(
                                            onClick = onOpenUpgrade,
                                            colors = ButtonDefaults.buttonColors(containerColor = SimaxEmber),
                                            shape = RoundedCornerShape(20.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("الترقية إلى النسخة الاحترافية (Pro)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }

                                        // License key input
                                        Column {
                                            Text(text = "تفعيل باستخدام مفتاح الرخصة", color = SimaxTextZinc500, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = inputKey,
                                                    onValueChange = {
                                                        inputKey = it
                                                        keyError = false
                                                    },
                                                    placeholder = { Text("SIMAX-XXXX-XXXX-XXXX", fontSize = 10.sp, color = SimaxTextZinc500) },
                                                    singleLine = true,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = SimaxBlood,
                                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                )

                                                Button(
                                                    onClick = {
                                                        val ok = onActivateKey(inputKey)
                                                        if (ok) {
                                                            Toast.makeText(context, "تم التفعيل بنجاح!", Toast.LENGTH_SHORT).show()
                                                            inputKey = ""
                                                        } else {
                                                            keyError = true
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SimaxBlood),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("تفعيل", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (keyError) {
                                                Text(
                                                    text = "مفتاح التفعيل غير صحيح أو منتهي الصلاحية.",
                                                    color = SimaxBlood2,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                onDeactivateSubscription()
                                                Toast.makeText(context, "تم إلغاء تفعيل الاشتراك", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().border(1.dp, SimaxBlood.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        ) {
                                            Text("إلغاء التفعيل على هذا الجهاز", color = SimaxBlood2, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            SettingsTab.DATA -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    DataActionRow(
                                        title = "تصدير المحادثات",
                                        desc = "تنزيل سجل كل الجلسات كملف بيانات محفوظ.",
                                        buttonText = "تصدير",
                                        isDanger = false,
                                        onClick = onExportData
                                    )

                                    DataActionRow(
                                        title = "مسح جميع المحادثات",
                                        desc = "حذف سجل كل الجلسات والبدء من جديد.",
                                        buttonText = "مسح الكل",
                                        isDanger = true,
                                        onClick = {
                                            onClearConversations()
                                            Toast.makeText(context, "تم مسح جميع المحادثات", Toast.LENGTH_SHORT).show()
                                        }
                                    )

                                    DataActionRow(
                                        title = "مسح الذاكرة المكتسبة",
                                        desc = "نسيان كل المعلومات والحقائق المخزنة عنك.",
                                        buttonText = "نسيان",
                                        isDanger = true,
                                        onClick = {
                                            onClearMemory()
                                            Toast.makeText(context, "تم مسح الذاكرة", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }

                            SettingsTab.ABOUT -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        SimaxLogo(size = 40.dp, hasGlow = true)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = "SIMAX AI", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "by SIMAX TEAM", color = SimaxTextZinc500, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }

                                    Text(
                                        text = "SIMAX ليس مجرد نافذة دردشة تقليدية، بل هو كيان ذكاء فائق متطور، صارم في تفكيره ومنطقه الرياضي والبرمجي، مصمم ليتفوق على أعتى منصات الذكاء العالمية.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )

                                    Text(
                                        text = "تطوير وإشراف: هيثم بن خليفة (Haithem ben Khelifa) - الجزائر، الجلفة (مسعد).",
                                        color = SimaxBlood2,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(text = "المحرك: S TURBO Multi-Tier Intelligence Engine", color = SimaxBlood2, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                            Text(text = "الرؤية والتحليل: Multimodal Vision & Document Data Engine", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                            Text(text = "توليد الصور: Pollinations Flux & DALL-E Realism", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                            Text(text = "البحث المباشر: Real-time Grounding & Verified Citations", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataActionRow(
    title: String,
    desc: String,
    buttonText: String,
    isDanger: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = SimaxTextZinc500, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDanger) SimaxBlood.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isDanger) SimaxBlood.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(6.dp)
                )
            ) {
                Text(
                    text = buttonText,
                    color = if (isDanger) SimaxBlood2 else MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
