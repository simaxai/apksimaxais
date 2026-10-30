package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SimaxBlood
import com.example.ui.theme.SimaxBlood2
import com.example.ui.theme.SimaxCrimson
import com.example.ui.theme.SimaxEmber
import com.example.ui.theme.SimaxLightText
import com.example.ui.theme.SimaxLine
import com.example.ui.theme.SimaxPanel2
import com.example.ui.theme.SimaxSignal
import com.example.ui.theme.SimaxTextWhite
import com.example.ui.theme.SimaxTextZinc300
import com.example.ui.theme.SimaxTextZinc400
import com.example.ui.theme.SimaxTextZinc500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class MarkdownBlock {
    data class Code(val language: String, val code: String) : MarkdownBlock()
    data class MathBlock(val rawLatex: String, val formatted: String) : MarkdownBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class BlockQuote(val text: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
    data class NumberedItem(val number: String, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class CopyableBox(val title: String, val content: String) : MarkdownBlock()
}

object MarkdownParser {

    fun parse(rawText: String): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        val lines = rawText.split("\n")
        var inCode = false
        var codeLang = ""
        val codeBuffer = mutableListOf<String>()

        var inMathBlock = false
        val mathBuffer = mutableListOf<String>()

        var inTable = false
        val tableHeaders = mutableListOf<String>()
        val tableRows = mutableListOf<List<String>>()

        fun flushTable() {
            if (inTable && tableHeaders.isNotEmpty()) {
                blocks.add(MarkdownBlock.Table(tableHeaders.toList(), tableRows.toList()))
                tableHeaders.clear()
                tableRows.clear()
                inTable = false
            }
        }

        for (line in lines) {
            val trimmed = line.trim()

            // 1. Handle Code blocks
            if (trimmed.startsWith("```")) {
                flushTable()
                if (inCode) {
                    blocks.add(MarkdownBlock.Code(codeLang, codeBuffer.joinToString("\n")))
                    codeBuffer.clear()
                    inCode = false
                    codeLang = ""
                } else {
                    inCode = true
                    codeLang = trimmed.removePrefix("```").trim()
                }
                continue
            }

            if (inCode) {
                codeBuffer.add(line)
                continue
            }

            // 2. Handle Multiline LaTeX Math blocks $$ ... $$
            if (trimmed == "$$" || trimmed == "\\[" || trimmed == "\\]") {
                flushTable()
                if (inMathBlock) {
                    val raw = mathBuffer.joinToString(" ")
                    blocks.add(MarkdownBlock.MathBlock(raw, formatLatexMath(raw)))
                    mathBuffer.clear()
                    inMathBlock = false
                } else {
                    inMathBlock = true
                }
                continue
            }

            if (inMathBlock) {
                mathBuffer.add(line)
                continue
            }

            if (trimmed.isEmpty()) {
                flushTable()
                continue
            }

            // 3. Handle Single-line LaTeX block $$equation$$ or mixed "Title: $$equation$$"
            if (trimmed.startsWith("$$") && trimmed.endsWith("$$") && trimmed.length > 4) {
                flushTable()
                val raw = trimmed.removeSurrounding("$$").trim()
                blocks.add(MarkdownBlock.MathBlock(raw, formatLatexMath(raw)))
                continue
            }

            if (trimmed.contains("$$")) {
                flushTable()
                val parts = trimmed.split("$$")
                if (parts.size >= 3) {
                    val prefix = parts[0].trim()
                    val equation = parts[1].trim()
                    val suffix = parts.subList(2, parts.size).joinToString("").trim()
                    if (prefix.isNotEmpty()) {
                        blocks.add(MarkdownBlock.Paragraph(prefix))
                    }
                    if (equation.isNotEmpty()) {
                        blocks.add(MarkdownBlock.MathBlock(equation, formatLatexMath(equation)))
                    }
                    if (suffix.isNotEmpty()) {
                        blocks.add(MarkdownBlock.Paragraph(suffix))
                    }
                    continue
                }
            }

            // 4. Handle Markdown Tables (| Col 1 | Col 2 |)
            if (trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2) {
                val cells = trimmed.split("|")
                    .map { it.trim() }
                    .filterIndexed { index, _ -> index != 0 && index != trimmed.split("|").lastIndex }

                // Check if this is a separator line e.g. |---|---|
                val isSeparator = cells.all { cell -> cell.all { ch -> ch == '-' || ch == ':' || ch == ' ' } }
                if (isSeparator) {
                    // separator line confirms we are in a valid table
                    inTable = true
                    continue
                }

                if (!inTable) {
                    // This is the header row
                    inTable = true
                    tableHeaders.clear()
                    tableHeaders.addAll(cells)
                } else {
                    // This is a data row
                    tableRows.add(cells)
                }
                continue
            } else {
                flushTable()
            }

            // 5. Markdown Headings
            when {
                trimmed.startsWith("### ") -> blocks.add(MarkdownBlock.Heading(3, trimmed.removePrefix("### ")))
                trimmed.startsWith("## ") -> blocks.add(MarkdownBlock.Heading(2, trimmed.removePrefix("## ")))
                trimmed.startsWith("# ") -> blocks.add(MarkdownBlock.Heading(1, trimmed.removePrefix("# ")))
                trimmed.startsWith("> ") -> blocks.add(MarkdownBlock.BlockQuote(trimmed.removePrefix("> ")))
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> blocks.add(MarkdownBlock.BulletItem(trimmed.substring(2)))
                Regex("""^\d+\.\s""").containsMatchIn(trimmed) -> {
                    val num = trimmed.substringBefore(".")
                    val content = trimmed.substringAfter(". ").trim()
                    blocks.add(MarkdownBlock.NumberedItem(num, content))
                }
                // Copyable command block e.g. `$ curl ...` or `npm install ...` or `pip install ...`
                (trimmed.startsWith("$ ") || trimmed.startsWith("npm ") || trimmed.startsWith("pip ") || trimmed.startsWith("git ")) -> {
                    blocks.add(MarkdownBlock.CopyableBox("COMMAND", trimmed.removePrefix("$ ")))
                }
                else -> blocks.add(MarkdownBlock.Paragraph(line))
            }
        }

        flushTable()

        if (inCode && codeBuffer.isNotEmpty()) {
            blocks.add(MarkdownBlock.Code(codeLang, codeBuffer.joinToString("\n")))
        }

        if (inMathBlock && mathBuffer.isNotEmpty()) {
            val raw = mathBuffer.joinToString(" ")
            blocks.add(MarkdownBlock.MathBlock(raw, formatLatexMath(raw)))
        }

        return blocks
    }
}

/**
 * Transforms LaTeX mathematical syntax into readable, formatted Unicode mathematical typography.
 */
fun formatLatexMath(latex: String): String {
    var s = latex.trim()

    // 1. Greek letters & symbols
    val greekMap = mapOf(
        "\\Delta" to "Δ", "\\delta" to "δ",
        "\\alpha" to "α", "\\beta" to "β",
        "\\gamma" to "γ", "\\Gamma" to "Γ",
        "\\theta" to "θ", "\\Theta" to "Θ",
        "\\lambda" to "λ", "\\Lambda" to "Λ",
        "\\mu" to "μ", "\\pi" to "π", "\\Pi" to "Π",
        "\\sigma" to "σ", "\\Sigma" to "Σ",
        "\\omega" to "ω", "\\Omega" to "Ω",
        "\\phi" to "φ", "\\Phi" to "Φ",
        "\\epsilon" to "ε", "\\eta" to "η"
    )
    for ((k, v) in greekMap) {
        s = s.replace(k, v)
    }

    // 2. Number sets
    s = s.replace("\\mathbb{C}", "ℂ")
        .replace("\\mathbb{R}", "ℝ")
        .replace("\\mathbb{N}", "ℕ")
        .replace("\\mathbb{Z}", "ℤ")
        .replace("\\mathbb{Q}", "ℚ")
        .replace("\\mathbf{R}", "ℝ")

    // 3. Operators & relations
    val opMap = mapOf(
        "\\pm" to "±", "\\mp" to "∓",
        "\\times" to "×", "\\cdot" to "·", "\\div" to "÷",
        "\\le" to "≤", "\\leq" to "≤",
        "\\ge" to "≥", "\\geq" to "≥",
        "\\neq" to "≠", "\\ne" to "≠",
        "\\approx" to "≈", "\\equiv" to "≡",
        "\\infty" to "∞", "\\in" to "∈", "\\notin" to "∉",
        "\\subset" to "⊂", "\\supset" to "⊃",
        "\\cup" to "∪", "\\cap" to "∩",
        "\\forall" to "∀", "\\exists" to "∃",
        "\\to" to "→", "\\rightarrow" to "→",
        "\\leftarrow" to "←", "\\leftrightarrow" to "↔",
        "\\implies" to "⟹", "\\iff" to "⟺",
        "\\sum" to "∑", "\\prod" to "∏", "\\int" to "∫",
        "\\partial" to "∂", "\\nabla" to "∇"
    )
    for ((k, v) in opMap) {
        s = s.replace(k, v)
    }

    // 4. Spacing
    s = s.replace("\\qquad", "      ")
        .replace("\\quad", "   ")
        .replace("\\,", " ")
        .replace("\\;", " ")
        .replace("\\!", "")

    // 5. Delimiters
    s = s.replace("\\left(", "(")
        .replace("\\right)", ")")
        .replace("\\left[", "[")
        .replace("\\right]", "]")
        .replace("\\left\\{", "{")
        .replace("\\right\\}", "}")
        .replace("\\left|", "|")
        .replace("\\right|", "|")

    // 6. Roots: \sqrt{expr} -> √(expr)
    s = Regex("""\\sqrt\{([^}]+)\}""").replace(s) { match ->
        "√(${match.groupValues[1]})"
    }
    s = Regex("""\\sqrt\[([^\]]+)\]\{([^}]+)\}""").replace(s) { match ->
        "ⁿ√(${match.groupValues[2]})".replace("ⁿ", match.groupValues[1])
    }

    // 7. Fractions: \frac{a}{b} -> (a) / (b)
    s = Regex("""\\frac\{([^}]+)\}\{([^}]+)\}""").replace(s) { match ->
        val num = match.groupValues[1].trim()
        val den = match.groupValues[2].trim()
        val cleanNum = if (num.length > 1 && !num.startsWith("(") && (num.contains("+") || num.contains("-"))) "($num)" else num
        val cleanDen = if (den.length > 1 && !den.startsWith("(") && (den.contains("+") || den.contains("-"))) "($den)" else den
        "$cleanNum / $cleanDen"
    }

    // 8. Superscripts (exponents)
    val supDigits = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻', '=' to '⁼', '(' to '⁽', ')' to '⁾',
        'n' to 'ⁿ', 'i' to 'ⁱ', 'x' to 'ˣ'
    )
    s = Regex("""\^\{([^}]+)\}""").replace(s) { match ->
        val inner = match.groupValues[1]
        inner.map { ch -> supDigits[ch] ?: ch }.joinToString("")
    }
    s = Regex("""\^([0-9nix+\-])""").replace(s) { match ->
        val ch = match.groupValues[1][0]
        (supDigits[ch] ?: ch).toString()
    }

    // 9. Subscripts
    val subDigits = mapOf(
        '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄',
        '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉',
        '+' to '₊', '-' to '₋', '=' to '₌', '(' to '₍', ')' to '₎',
        'a' to 'ₐ', 'e' to 'ₑ', 'o' to 'ₒ', 'x' to 'ₓ', 'i' to 'ᵢ', 'j' to 'ⱼ'
    )
    s = Regex("""_\{([^}]+)\}""").replace(s) { match ->
        val inner = match.groupValues[1]
        inner.map { ch -> subDigits[ch] ?: ch }.joinToString("")
    }
    s = Regex("""_([0-9aeoxij+\-])""").replace(s) { match ->
        val ch = match.groupValues[1][0]
        (subDigits[ch] ?: ch).toString()
    }

    // 10. Clean up backslashes before plain letters
    s = s.replace(Regex("""\\([a-zA-Z]+)""")) { it.groupValues[1] }

    return s
}

@Composable
fun MarkdownContent(
    text: String,
    modifier: Modifier = Modifier
) {
    val blocks = remember(text) { MarkdownParser.parse(text) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (block in blocks) {
            when (block) {
                is MarkdownBlock.Code -> {
                    GeminiStyleCodeCard(code = block.code, language = block.language)
                }
                is MarkdownBlock.MathBlock -> {
                    MathEquationCard(rawLatex = block.rawLatex, formatted = block.formatted)
                }
                is MarkdownBlock.Table -> {
                    RenderMarkdownTable(headers = block.headers, rows = block.rows)
                }
                is MarkdownBlock.CopyableBox -> {
                    CopyableSnippetCard(title = block.title, content = block.content)
                }
                is MarkdownBlock.Heading -> {
                    RenderHeading(level = block.level, text = block.text)
                }
                is MarkdownBlock.BlockQuote -> {
                    RenderQuote(text = block.text)
                }
                is MarkdownBlock.BulletItem -> {
                    RenderBullet(text = block.text)
                }
                is MarkdownBlock.NumberedItem -> {
                    RenderNumbered(number = block.number, text = block.text)
                }
                is MarkdownBlock.Paragraph -> {
                    RenderRichParagraph(text = block.text)
                }
            }
        }
    }
}

/**
 * Beautiful Math Equation Card for LaTeX equations (like ChatGPT and Gemini)
 */
@Composable
fun MathEquationCard(
    rawLatex: String,
    formatted: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, SimaxBlood.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ModernIcons.Formula,
                        contentDescription = "Equation",
                        tint = SimaxBlood2,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "صيغة رياضية (EQUATION)",
                        color = SimaxBlood2,
                        fontSize = 10.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("LaTeX", rawLatex))
                            copied = true
                            scope.launch {
                                delay(1400L)
                                copied = false
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (copied) ModernIcons.Check else ModernIcons.Copy,
                        contentDescription = "Copy LaTeX",
                        tint = if (copied) SimaxBlood2 else SimaxTextZinc400,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "تم النسخ" else "LaTeX",
                        color = if (copied) SimaxBlood2 else SimaxTextZinc400,
                        fontSize = 10.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Centered formatted mathematical equation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatted,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

/**
 * Beautiful Table Card for Markdown tables
 */
@Composable
fun RenderMarkdownTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
    ) {
        Box(modifier = Modifier.horizontalScroll(scrollState)) {
            Column {
                // Header row
                Row(
                    modifier = Modifier
                        .background(SimaxBlood.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    headers.forEachIndexed { i, h ->
                        Text(
                            text = h,
                            color = SimaxBlood2,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily,
                            modifier = Modifier
                                .width(120.dp)
                                .padding(horizontal = 6.dp)
                        )
                    }
                }

                HorizontalDivider(color = SimaxBlood.copy(alpha = 0.3f), thickness = 1.dp)

                // Data rows
                rows.forEachIndexed { rowIndex, row ->
                    val isEven = rowIndex % 2 == 0
                    Row(
                        modifier = Modifier
                            .background(if (isEven) Color.Transparent else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { cellIndex, cell ->
                            Text(
                                text = cell,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontFamily = InterFontFamily,
                                modifier = Modifier
                                    .width(120.dp)
                                    .padding(horizontal = 6.dp)
                            )
                        }
                    }
                    if (rowIndex < rows.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun GeminiStyleCodeCard(
    code: String,
    language: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }
    val horizontalScroll = rememberScrollState()
    val lines = remember(code) { code.split("\n") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070708))
            .border(1.dp, SimaxBlood.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SimaxPanel2)
                .border(1.dp, SimaxLine, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Colored status dots
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SimaxBlood))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SimaxEmber))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SimaxSignal))
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = language.ifBlank { "CODE" }.uppercase(),
                    color = SimaxBlood2,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            // Copy button with feedback
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (copied) SimaxBlood.copy(alpha = 0.2f) else Color(0xFF141414))
                    .border(1.dp, if (copied) SimaxBlood2 else SimaxLine, RoundedCornerShape(6.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("code", code))
                        copied = true
                        scope.launch {
                            delay(1600L)
                            copied = false
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (copied) ModernIcons.Check else ModernIcons.Copy,
                    contentDescription = "Copy code",
                    tint = if (copied) SimaxBlood2 else SimaxTextZinc400,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (copied) "تم النسخ" else "نسخ الكود",
                    color = if (copied) SimaxBlood2 else SimaxTextZinc300,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Code body with line numbering and horizontal scrolling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Line numbers column
            Column(
                modifier = Modifier.padding(end = 12.dp)
            ) {
                lines.indices.forEach { index ->
                    Text(
                        text = "${index + 1}".padStart(2, ' '),
                        color = SimaxTextZinc500,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        lineHeight = 18.sp
                    )
                }
            }

            // Scrollable code content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScroll)
            ) {
                Column {
                    lines.forEach { lineText ->
                        Text(
                            text = highlightSyntax(lineText, language),
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CopyableSnippetCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0C0C0E))
            .border(1.dp, SimaxEmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(title, content))
                copied = true
                scope.launch {
                    delay(1500L)
                    copied = false
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SimaxEmber,
                fontSize = 9.sp,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = content,
                color = SimaxTextWhite,
                fontSize = 13.sp,
                fontFamily = JetBrainsMonoFontFamily
            )
        }

        Icon(
            imageVector = if (copied) ModernIcons.Check else ModernIcons.Copy,
            contentDescription = "Copy",
            tint = if (copied) SimaxBlood2 else SimaxTextZinc400,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun RenderHeading(level: Int, text: String) {
    val headingColor = if (level == 1) MaterialTheme.colorScheme.onSurface else if (level == 2) SimaxBlood2 else SimaxEmber
    val (size, weight) = when (level) {
        1 -> Pair(20.sp, FontWeight.Bold)
        2 -> Pair(17.sp, FontWeight.Bold)
        else -> Pair(15.sp, FontWeight.SemiBold)
    }

    Row(
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((size.value + 4).dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SimaxBlood)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = size,
            color = headingColor,
            fontWeight = weight,
            fontFamily = InterFontFamily
        )
    }
}

@Composable
fun RenderQuote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(SimaxBlood.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
            .border(1.dp, SimaxBlood.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(SimaxBlood)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = InterFontFamily,
            lineHeight = 19.sp
        )
    }
}

@Composable
fun RenderBullet(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(SimaxBlood2)
        )
        Spacer(modifier = Modifier.width(8.dp))
        RenderRichParagraph(text = text, modifier = Modifier.weight(1f))
    }
}

@Composable
fun RenderNumbered(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$number.",
            color = SimaxBlood2,
            fontSize = 13.sp,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(22.dp)
        )
        RenderRichParagraph(text = text, modifier = Modifier.weight(1f))
    }
}

@Composable
fun RenderRichParagraph(
    text: String,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val isLight = textColor == SimaxLightText

    val annotated = remember(text, isLight) {
        buildAnnotatedString {
            var i = 0
            while (i < text.length) {
                // 1. Check inline math $...$
                if (text[i] == '$' && !text.substring(i).startsWith("$$")) {
                    val endIdx = text.indexOf('$', i + 1)
                    if (endIdx != -1) {
                        val rawMath = text.substring(i + 1, endIdx)
                        val formattedMath = formatLatexMath(rawMath)
                        withStyle(
                            SpanStyle(
                                color = SimaxBlood2,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(formattedMath)
                        }
                        i = endIdx + 1
                        continue
                    }
                }

                // 2. Check inline code `code`
                if (text[i] == '`' && !text.substring(i).startsWith("```")) {
                    val endIdx = text.indexOf('`', i + 1)
                    if (endIdx != -1) {
                        withStyle(
                            SpanStyle(
                                color = SimaxBlood2,
                                background = if (isLight) Color(0xFFE8E8EE) else Color(0xFF18181A),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(" ${text.substring(i + 1, endIdx)} ")
                        }
                        i = endIdx + 1
                        continue
                    }
                }

                // 3. Check bold **text**
                if (text.substring(i).startsWith("**")) {
                    val endIdx = text.indexOf("**", i + 2)
                    if (endIdx != -1) {
                        withStyle(
                            SpanStyle(
                                color = if (isLight) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(text.substring(i + 2, endIdx))
                        }
                        i = endIdx + 2
                        continue
                    }
                }

                // Normal character
                append(text[i])
                i++
            }
        }
    }

    Text(
        text = annotated,
        color = textColor,
        fontSize = 14.sp,
        fontFamily = InterFontFamily,
        lineHeight = 22.sp,
        modifier = modifier
    )
}

fun highlightSyntax(line: String, language: String): androidx.compose.ui.text.AnnotatedString {
    val keywords = setOf(
        "val", "var", "fun", "class", "interface", "object", "return", "import", "package",
        "if", "else", "when", "for", "while", "try", "catch", "finally", "throw", "null", "true", "false",
        "def", "import", "from", "as", "const", "let", "function", "async", "await", "public", "private"
    )

    return buildAnnotatedString {
        val trimmed = line.trimStart()
        if (trimmed.startsWith("//") || trimmed.startsWith("#")) {
            withStyle(SpanStyle(color = SimaxTextZinc500, fontStyle = FontStyle.Italic)) {
                append(line)
            }
            return@buildAnnotatedString
        }

        val words = line.split(Regex("(?<=\\s)|(?=\\s)|(?<=[(),;=])|(?=[(),;=])"))
        for (token in words) {
            when {
                keywords.contains(token) -> {
                    withStyle(SpanStyle(color = SimaxSignal, fontWeight = FontWeight.Bold)) {
                        append(token)
                    }
                }
                token.startsWith("\"") || token.endsWith("\"") || token.startsWith("'") -> {
                    withStyle(SpanStyle(color = SimaxEmber)) {
                        append(token)
                    }
                }
                token.toDoubleOrNull() != null -> {
                    withStyle(SpanStyle(color = SimaxBlood2)) {
                        append(token)
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = SimaxTextWhite)) {
                        append(token)
                    }
                }
            }
        }
    }
}
