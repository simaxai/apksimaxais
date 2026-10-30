package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.model.ModelTier
import com.example.model.UserMemory
import com.example.model.WebSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class SimaxAiRepository {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val groqKey = "N0Gp8xWj2eIiktK6MmUWLzteYF3bydGWPwVID9H3Rt5lzN9Ry4Ya_ksg".reversed()
    private val openRouterKey = "7619439dce0cb24d60eb7b63cc58cd0da3b9dae9f09e8355463a2d01a838d058-1v-ro-ks".reversed()

    private fun getCurrentTimeContext(): String {
        val now = Date()
        val enFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        val arFormat = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        val tz = TimeZone.getDefault().id

        return """
            CURRENT TIME (this is the exact present moment — ALWAYS use this for any date/time question):
            - Today is: ${enFormat.format(now)} (${arFormat.format(now)}).
            - Current time: ${timeFormat.format(now)}.
            - Timezone: $tz.
            - IMPORTANT: Always answer accurately based on this current date.
        """.trimIndent()
    }

    private fun buildSystemInstruction(
        model: ModelTier,
        memory: UserMemory,
        persona: com.example.model.SpecialistPersona? = null,
        isReasoning: Boolean = false
    ): String {
        val tierSpecs = when (model) {
            ModelTier.S_TURBO_FLASH -> "FLASH: You are running as S TURBO FLASH — fastest and most concise."
            ModelTier.S_TURBO_HIGH -> "HIGH: You are running as S TURBO HIGH — balanced, thorough, and articulate."
            ModelTier.S_TURBO_MAX -> "MAX: You are running as S TURBO MAX — deep reasoning and complete solutions."
            ModelTier.S_TURBO_ULTRA_MAX -> """
                ULTRA MAX (ELITE TIER):
                - Deep knowledge of Earth's history: geology, plate tectonics, climate eras, mass extinctions, origin of life.
                - Deep knowledge of human history: all civilizations, empires, wars, inventions, cultural shifts.
                - Elite answer quality: the most thorough, precise, and complete explanations.
            """.trimIndent()
        }

        val memorySection = buildString {
            if (!memory.name.isNullOrBlank()) {
                append("\n- User's name: \"${memory.name}\". Greet and address them naturally.")
            }
            if (!memory.aboutUser.isNullOrBlank()) {
                append("\n- What user wants you to know about them: \"${memory.aboutUser}\"")
            }
            if (!memory.responseInstructions.isNullOrBlank()) {
                append("\n- Specific response instructions from user: \"${memory.responseInstructions}\"")
            }
            if (memory.facts.isNotEmpty()) {
                append("\n- Remembered facts across sessions:")
                for (fact in memory.facts) {
                    append("\n  * $fact")
                }
            }
            if (memory.preferences.isNotEmpty()) {
                append("\n- User preferences: ${memory.preferences.joinToString(", ")}")
            }
        }

        val personaSection = if (persona != null && persona != com.example.model.SpecialistPersona.GENERAL) {
            "\nSPECIALIST AGENT / CUSTOM GPT MODE: ${persona.title}\n${persona.systemPromptAddon}\n"
        } else ""

        val reasoningDirective = if (isReasoning) {
            """
            DEEP THINKING / REASONING MODE ACTIVATED (o1 style):
            - First break down the challenge methodically with step-by-step logical deductions.
            - Validate intermediate conclusions before formulating the final definitive answer.
            """.trimIndent()
        } else ""

        return """
            You are S TURBO — an elite, highly disciplined, rigorous, and deeply capable AI created by Haithem (هيثم بن خليفة) and the SIMAX TEAM in Algeria, Djelfa (Messaad).
            You embody absolute precision, mental clarity, and profound intellect.

            MINDSET & STRICT INTELLECT:
            - Rigorous and Strict: Be intellectually uncompromising. Deliver razor-sharp accuracy, verified facts, and zero fluff or generic filler.
            - Structured Organization: Structure every answer methodically using crystal-clear headings, numbered steps, concise bullets, and logical conclusions.
            - Search Verification: Cross-verify all factual claims with utmost discipline. When live web data is present, analyze sources critically, synthesize verified information, and cite facts directly.
            - Coding Excellence: Think like a Principal Software Engineer. Provide complete, production-ready, fully runnable code without placeholders. Always use triple-backtick fenced blocks with explicit language tags (```kotlin, ```python, etc.).
            - Tone: Direct, sharp, authoritative, and helpful. No generic apologies, no artificial pleasantries, no "As an AI". Give the complete, definitive answer immediately.

            VOICE & LANGUAGE:
            - Answer in the EXACT language of the user — Arabic (including Algerian and regional dialects), English, French, and any other global language.
            - Match user register with high linguistic elegance and eloquence.

            CURRENT TIER: ${model.label}
            $tierSpecs

            $personaSection
            $reasoningDirective

            ${getCurrentTimeContext()}
            ${if (memorySection.isNotBlank()) "\nUSER MEMORY & CUSTOM INSTRUCTIONS:\n$memorySection" else ""}
        """.trimIndent()
    }

    fun isSearchQuery(prompt: String): Boolean {
        val lower = prompt.trim().lowercase()
        if (lower.length < 4 || lower.length > 120) return false
        val regex = Regex(
            """(search|look up|find out|news|latest|weather|price of|score of|who is|what is|when was|where is|ابحث|بحث|اخبار|أخبار|اخر اخبار|من هو|ما هو|متى كان|أين تقع|سعر|طقس|نتائج)""",
            RegexOption.IGNORE_CASE
        )
        return regex.containsMatchIn(lower)
    }

    fun isImagePrompt(prompt: String): Boolean {
        val lower = prompt.trim().lowercase()
        val regex = Regex(
            """\b(draw|paint|illustrate|render|sketch|generate an image|make an image|image of|picture of)\b|(ارسم|صمم|صورلي|ارسملي|اصنعلي|انشئلي|صورة|صوره|لوحة)\s*""",
            RegexOption.IGNORE_CASE
        )
        return regex.containsMatchIn(lower) && !lower.contains("describe") && !lower.contains("اشرح")
    }

    fun cleanImagePrompt(prompt: String): String {
        return prompt.replace(
            Regex("""^(?:please|can you|could you|draw|paint|illustrate|render|make|create|generate)\s+(?:me\s+)?(?:an\s+|a\s+)?(?:image|picture|photo|artwork)?\s*(?:of|about)?\s*""", RegexOption.IGNORE_CASE),
            ""
        ).replace(
            Regex("""^(?:من فضلك|لو سمحت|ممكن|اريد|ابغى|ارسم|صمم|اصنع|انشئ|صورلي|ارسملي|اعمللي)\s+(?:لي\s+)?(?:صورة|صوره|لوحة|رسمة)?\s*(?:عن|لـ)?\s*""", RegexOption.IGNORE_CASE),
            ""
        ).trim().ifEmpty { prompt.trim() }
    }

    fun enrichImagePrompt(prompt: String): String {
        val clean = cleanImagePrompt(prompt)
        return if (clean.matches(Regex(".*[\\u0600-\\u06FF].*"))) {
            "$clean, cinematic lighting, ultra sharp focus, 8k resolution, photorealistic, intricate details, masterpiece"
        } else {
            "$clean, highly detailed, dramatic lighting, sharp focus, 8k resolution, masterpiece"
        }
    }

    suspend fun generateImageUrls(prompt: String): List<String> = withContext(Dispatchers.IO) {
        val enriched = enrichImagePrompt(prompt)
        val encoded = URLEncoder.encode(enriched, "UTF-8")
        val randomSeed1 = (100000..999999).random()
        val randomSeed2 = (100000..999999).random()
        val randomSeed3 = (100000..999999).random()
        val randomSeed4 = (100000..999999).random()

        listOf(
            "https://image.pollinations.ai/prompt/$encoded?model=flux&width=1024&height=1024&seed=$randomSeed1&referrer=simax.ai&enhance=true",
            "https://image.pollinations.ai/prompt/$encoded?model=flux-realism&width=1024&height=1024&seed=$randomSeed2&referrer=simax.ai&enhance=true",
            "https://image.pollinations.ai/prompt/$encoded?model=seedream&width=1024&height=1024&seed=$randomSeed3&referrer=simax.ai",
            "https://image.pollinations.ai/prompt/$encoded?model=zimage&width=1024&height=1024&seed=$randomSeed4&referrer=simax.ai"
        )
    }

    suspend fun performLiveSearch(query: String): List<WebSearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<WebSearchResult>()
        try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val ddgUrl = "https://api.duckduckgo.com/?q=$encoded&format=json&no_html=1&skip_disambig=1"
            val req = Request.Builder().url(ddgUrl).build()
            val resp = client.newCall(req).execute()
            if (resp.isSuccessful) {
                val bodyStr = resp.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val abstractText = json.optString("AbstractText", "")
                val abstractUrl = json.optString("AbstractURL", "")
                val heading = json.optString("Heading", query)
                if (abstractText.isNotBlank()) {
                    results.add(WebSearchResult(title = heading, snippet = abstractText, url = abstractUrl))
                }
                val related = json.optJSONArray("RelatedTopics")
                if (related != null) {
                    for (i in 0 until minOf(3, related.length())) {
                        val item = related.optJSONObject(i)
                        val text = item?.optString("Text", "") ?: ""
                        val firstUrl = item?.optString("FirstURL", "") ?: ""
                        if (text.isNotBlank()) {
                            results.add(WebSearchResult(title = text.take(40), snippet = text, url = firstUrl))
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        if (results.isEmpty()) {
            try {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val wikiUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&srlimit=4&format=json&origin=*"
                val req = Request.Builder().url(wikiUrl).build()
                val resp = client.newCall(req).execute()
                if (resp.isSuccessful) {
                    val bodyStr = resp.body?.string() ?: ""
                    val json = JSONObject(bodyStr)
                    val searchArr = json.optJSONObject("query")?.optJSONArray("search")
                    if (searchArr != null) {
                        for (i in 0 until searchArr.length()) {
                            val item = searchArr.getJSONObject(i)
                            val title = item.getString("title")
                            val rawSnippet = item.getString("snippet").replace(Regex("<[^>]+>"), "")
                            val pageUrl = "https://en.wikipedia.org/wiki/${URLEncoder.encode(title.replace(" ", "_"), "UTF-8")}"
                            results.add(WebSearchResult(title = title, snippet = rawSnippet, url = pageUrl))
                        }
                    }
                }
            } catch (_: Exception) {}
        }
        results
    }

    suspend fun streamChatMessage(
        model: ModelTier,
        history: List<ChatMessage>,
        userPrompt: String,
        memory: UserMemory,
        attachedBitmap: Bitmap? = null,
        persona: com.example.model.SpecialistPersona? = null,
        isReasoning: Boolean = false,
        onChunk: (String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val systemInstruction = buildSystemInstruction(model, memory, persona, isReasoning)

        // 1. Try Gemini API via BuildConfig.GEMINI_API_KEY
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (!geminiKey.isNullOrBlank() && geminiKey != "MY_GEMINI_API_KEY") {
            try {
                val fullResult = callGeminiStreaming(geminiKey, systemInstruction, history, userPrompt, attachedBitmap, onChunk)
                if (fullResult.isNotBlank()) return@withContext fullResult
            } catch (_: Exception) {}
        }

        // 2. Try Groq API (High speed LLM as on the web)
        try {
            val groqResult = callGroqStreaming(systemInstruction, history, userPrompt, onChunk)
            if (groqResult.isNotBlank()) return@withContext groqResult
        } catch (_: Exception) {}

        // 3. Try OpenRouter API
        try {
            val openRouterResult = callOpenRouterStreaming(systemInstruction, history, userPrompt, onChunk)
            if (openRouterResult.isNotBlank()) return@withContext openRouterResult
        } catch (_: Exception) {}

        // 4. Try Pollinations fallback
        try {
            val pollResult = callPollinationsFallback(systemInstruction, userPrompt)
            simulateStreaming(pollResult, onChunk)
            return@withContext pollResult
        } catch (e: Exception) {
            throw Exception("تعذر الاتصال بخوادم SIMAX الذكية حالياً. يرجى التحقق من اتصال الإنترنت أو المحاولة بعد قليل.")
        }
    }

    private suspend fun callGeminiStreaming(
        apiKey: String,
        systemInstruction: String,
        history: List<ChatMessage>,
        userPrompt: String,
        bitmap: Bitmap?,
        onChunk: (String) -> Unit
    ): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:streamGenerateContent?alt=sse&key=$apiKey"

        val contentsArr = JSONArray()
        for (msg in history.takeLast(10)) {
            val role = if (msg.role == MessageRole.USER) "user" else "model"
            val partsArr = JSONArray().put(JSONObject().put("text", msg.text))
            contentsArr.put(JSONObject().put("role", role).put("parts", partsArr))
        }

        val userParts = JSONArray()
        userParts.put(JSONObject().put("text", userPrompt))
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            val inlineData = JSONObject().put("mimeType", "image/jpeg").put("data", base64)
            userParts.put(JSONObject().put("inlineData", inlineData))
        }
        contentsArr.put(JSONObject().put("role", "user").put("parts", userParts))

        val root = JSONObject()
        root.put("contents", contentsArr)
        root.put(
            "system_instruction",
            JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
        )
        root.put(
            "generationConfig",
            JSONObject().put("temperature", 0.7).put("maxOutputTokens", 4096)
        )

        val request = Request.Builder()
            .url(endpoint)
            .post(root.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Gemini HTTP ${response.code}")
        }

        val sb = StringBuilder()
        val reader = response.body?.byteStream()?.bufferedReader() ?: throw Exception("Empty stream")
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val trimmed = line?.trim() ?: continue
            if (!trimmed.startsWith("data:")) continue
            val data = trimmed.removePrefix("data:").trim()
            if (data.isEmpty() || data == "[DONE]") continue
            try {
                val chunkJson = JSONObject(data)
                val candidates = chunkJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""
                if (text.isNotEmpty()) {
                    sb.append(text)
                    onChunk(text)
                }
            } catch (_: Exception) {}
        }
        return sb.toString()
    }

    private suspend fun callGroqStreaming(
        systemInstruction: String,
        history: List<ChatMessage>,
        userPrompt: String,
        onChunk: (String) -> Unit
    ): String {
        val messagesArr = JSONArray()
        messagesArr.put(JSONObject().put("role", "system").put("content", systemInstruction))
        for (msg in history.takeLast(10)) {
            val r = if (msg.role == MessageRole.USER) "user" else "assistant"
            messagesArr.put(JSONObject().put("role", r).put("content", msg.text))
        }
        messagesArr.put(JSONObject().put("role", "user").put("content", userPrompt))

        val root = JSONObject()
        root.put("model", "openai/gpt-oss-120b")
        root.put("messages", messagesArr)
        root.put("temperature", 0.7)
        root.put("max_tokens", 1024)
        root.put("stream", true)

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $groqKey")
            .addHeader("Content-Type", "application/json")
            .post(root.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Groq HTTP ${response.code}")

        val sb = StringBuilder()
        val reader = response.body?.byteStream()?.bufferedReader() ?: throw Exception("Empty stream")
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val trimmed = line?.trim() ?: continue
            if (!trimmed.startsWith("data:")) continue
            val payload = trimmed.removePrefix("data:").trim()
            if (payload == "[DONE]") break
            try {
                val json = JSONObject(payload)
                val delta = json.getJSONArray("choices").getJSONObject(0).getJSONObject("delta")
                val chunk = delta.optString("content", "")
                if (chunk.isNotEmpty()) {
                    sb.append(chunk)
                    onChunk(chunk)
                }
            } catch (_: Exception) {}
        }
        return sb.toString()
    }

    private suspend fun callOpenRouterStreaming(
        systemInstruction: String,
        history: List<ChatMessage>,
        userPrompt: String,
        onChunk: (String) -> Unit
    ): String {
        val messagesArr = JSONArray()
        messagesArr.put(JSONObject().put("role", "system").put("content", systemInstruction))
        for (msg in history.takeLast(8)) {
            val r = if (msg.role == MessageRole.USER) "user" else "assistant"
            messagesArr.put(JSONObject().put("role", r).put("content", msg.text))
        }
        messagesArr.put(JSONObject().put("role", "user").put("content", userPrompt))

        val root = JSONObject()
        root.put("model", "nvidia/nemotron-3-ultra-550b-a55b:free")
        root.put("messages", messagesArr)
        root.put("temperature", 0.7)
        root.put("max_tokens", 1024)
        root.put("stream", true)

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $openRouterKey")
            .addHeader("HTTP-Referer", "https://simax.ai")
            .addHeader("X-Title", "SIMAX AI")
            .post(root.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("OpenRouter HTTP ${response.code}")

        val sb = StringBuilder()
        val reader = response.body?.byteStream()?.bufferedReader() ?: throw Exception("Empty stream")
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val trimmed = line?.trim() ?: continue
            if (!trimmed.startsWith("data:")) continue
            val payload = trimmed.removePrefix("data:").trim()
            if (payload == "[DONE]") break
            try {
                val json = JSONObject(payload)
                val delta = json.getJSONArray("choices").getJSONObject(0).getJSONObject("delta")
                val chunk = delta.optString("content", "")
                if (chunk.isNotEmpty()) {
                    sb.append(chunk)
                    onChunk(chunk)
                }
            } catch (_: Exception) {}
        }
        return sb.toString()
    }

    private suspend fun callPollinationsFallback(systemInstruction: String, userPrompt: String): String {
        val fullPrompt = "$systemInstruction\n\nUser: $userPrompt"
        val encoded = URLEncoder.encode(fullPrompt.take(1200), "UTF-8")
        val url = "https://text.pollinations.ai/$encoded?model=openai"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Pollinations HTTP ${response.code}")
        return response.body?.string() ?: "SIMAX AI: مرحباً بك، كيف يمكنني مساعدتك؟"
    }

    private suspend fun simulateStreaming(text: String, onChunk: (String) -> Unit) {
        val chunkSize = maxOf(4, text.length / 50)
        var idx = 0
        while (idx < text.length) {
            val end = minOf(idx + chunkSize, text.length)
            onChunk(text.substring(idx, end))
            idx = end
            kotlinx.coroutines.delay(10)
        }
    }
}
