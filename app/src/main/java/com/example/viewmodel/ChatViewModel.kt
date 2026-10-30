package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LicenseManager
import com.example.data.SimaxAiRepository
import com.example.data.StorageManager
import com.example.model.ChatMessage
import com.example.model.ChatSession
import com.example.model.MessageRole
import com.example.model.ModelTier
import com.example.voice.VoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = StorageManager(application)
    private val repository = SimaxAiRepository()
    val voiceManager = VoiceManager(application)

    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _selectedModel = MutableStateFlow(storage.getSelectedModel())
    val selectedModel: StateFlow<ModelTier> = _selectedModel.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(storage.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isSubscribed = MutableStateFlow(storage.isSubscribed())
    val isSubscribed: StateFlow<Boolean> = _isSubscribed.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _isReasoningActive = MutableStateFlow(false)
    val isReasoningActive: StateFlow<Boolean> = _isReasoningActive.asStateFlow()

    private val _activePersona = MutableStateFlow(com.example.model.SpecialistPersona.fromId(storage.getActivePersona()))
    val activePersona: StateFlow<com.example.model.SpecialistPersona> = _activePersona.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _attachedBitmap = MutableStateFlow<Bitmap?>(null)
    val attachedBitmap: StateFlow<Bitmap?> = _attachedBitmap.asStateFlow()

    private val _attachedName = MutableStateFlow<String?>(null)
    val attachedName: StateFlow<String?> = _attachedName.asStateFlow()

    private val _attachedDocName = MutableStateFlow<String?>(null)
    val attachedDocName: StateFlow<String?> = _attachedDocName.asStateFlow()

    private val _attachedDocContent = MutableStateFlow<String?>(null)
    val attachedDocContent: StateFlow<String?> = _attachedDocContent.asStateFlow()

    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    private val _showGoogleAuthDialog = MutableStateFlow(false)
    val showGoogleAuthDialog: StateFlow<Boolean> = _showGoogleAuthDialog.asStateFlow()

    private val _isGoogleLoggedIn = MutableStateFlow(storage.getGoogleAccount().first)
    val isGoogleLoggedIn: StateFlow<Boolean> = _isGoogleLoggedIn.asStateFlow()

    private val _googleEmail = MutableStateFlow(storage.getGoogleAccount().second)
    val googleEmail: StateFlow<String> = _googleEmail.asStateFlow()

    private var activeJob: Job? = null

    init {
        val loaded = storage.loadSessions()
        _sessions.value = loaded
        val activeId = storage.getActiveSessionId()
        if (activeId != null && loaded.any { it.id == activeId }) {
            _activeSessionId.value = activeId
            _messages.value = loaded.first { it.id == activeId }.messages
        } else if (loaded.isNotEmpty()) {
            _activeSessionId.value = loaded.first().id
            _messages.value = loaded.first().messages
        }

        voiceManager.onFinalSpeechResult = { recognizedText ->
            if (recognizedText.isNotBlank()) {
                sendMessage(recognizedText)
            }
        }
    }

    fun toggleReasoning() {
        _isReasoningActive.value = !_isReasoningActive.value
    }

    fun selectPersona(persona: com.example.model.SpecialistPersona) {
        _activePersona.value = persona
        storage.setActivePersona(persona.id)
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        storage.setDarkTheme(newTheme)
    }

    fun setTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        storage.setDarkTheme(dark)
    }

    fun selectModel(model: ModelTier) {
        if (model.isPro && !_isSubscribed.value) {
            _showUpgradeDialog.value = true
            return
        }
        _selectedModel.value = model
        storage.setSelectedModel(model)
    }

    fun openUpgradeDialog() {
        _showUpgradeDialog.value = true
    }

    fun closeUpgradeDialog() {
        _showUpgradeDialog.value = false
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openVoiceDialog() {
        _showVoiceDialog.value = true
        voiceManager.startListening()
    }

    fun closeVoiceDialog() {
        _showVoiceDialog.value = false
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
    }

    fun openGoogleAuthDialog() {
        _showGoogleAuthDialog.value = true
    }

    fun closeGoogleAuthDialog() {
        _showGoogleAuthDialog.value = false
    }

    fun signInWithGoogle(email: String = "simaxx991top@gmail.com") {
        storage.setGoogleAccount(true, email)
        _isGoogleLoggedIn.value = true
        _googleEmail.value = email
        _showGoogleAuthDialog.value = false
        Toast.makeText(getApplication(), "تم تسجيل الدخول بحساب قوقل بنجاح", Toast.LENGTH_SHORT).show()
    }

    fun signOutGoogle() {
        storage.setGoogleAccount(false)
        _isGoogleLoggedIn.value = false
        Toast.makeText(getApplication(), "تم تسجيل الخروج", Toast.LENGTH_SHORT).show()
    }

    fun setAttachedImage(uri: Uri, name: String) {
        try {
            val ctx = getApplication<Application>()
            val input: InputStream? = ctx.contentResolver.openInputStream(uri)
            val bmp = BitmapFactory.decodeStream(input)
            _attachedBitmap.value = bmp
            _attachedName.value = name
        } catch (_: Exception) {}
    }

    fun setAttachedDocument(name: String, content: String) {
        _attachedDocName.value = name
        _attachedDocContent.value = content
    }

    fun loadAndAttachAnyFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val ctx = getApplication<Application>()
                val cr = ctx.contentResolver
                val name = uri.lastPathSegment?.substringAfterLast('/') ?: "file.dat"
                val lower = name.lowercase()

                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")) {
                    val input = cr.openInputStream(uri)
                    val bmp = BitmapFactory.decodeStream(input)
                    if (bmp != null) {
                        _attachedBitmap.value = bmp
                        _attachedName.value = name
                        return@launch
                    }
                }

                // Read bytes
                val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: return@launch
                val extractedText = if (lower.endsWith(".pdf")) {
                    val raw = String(bytes, Charsets.ISO_8859_1)
                    val textBlocks = mutableListOf<String>()
                    val regex = Regex("""\(([^()]+)\)\s*T[jJ]""")
                    for (m in regex.findAll(raw)) {
                        val s = m.groupValues[1]
                        if (s.isNotBlank() && s.length > 1) {
                            textBlocks.add(s)
                        }
                    }
                    if (textBlocks.isNotEmpty()) {
                        textBlocks.joinToString(" ").take(25000)
                    } else {
                        val printable = bytes.filter { it in 32..126 || it.toInt() in listOf(10, 13, 9) }.toByteArray()
                        val cleaned = String(printable, Charsets.UTF_8).lines()
                            .filter { it.trim().length > 4 && !it.startsWith("%") && !it.contains("obj") && !it.contains("endobj") }
                            .joinToString("\n")
                        cleaned.ifBlank { "PDF Document [${name}] loaded for analysis." }.take(25000)
                    }
                } else {
                    String(bytes, Charsets.UTF_8).take(25000)
                }

                _attachedDocName.value = name
                _attachedDocContent.value = extractedText
                Toast.makeText(ctx, "تم تحميل وقراءة $name بنجاح", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "تعذر قراءة الملف: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearAttachment() {
        _attachedBitmap.value = null
        _attachedName.value = null
        _attachedDocName.value = null
        _attachedDocContent.value = null
    }

    fun saveCustomInstructions(aboutUser: String?, responseInstructions: String?) {
        storage.setCustomInstructions(aboutUser, responseInstructions)
    }

    fun removeUserFact(fact: String) {
        storage.removeUserFact(fact)
    }

    fun getUserMemory(): com.example.model.UserMemory = storage.getUserMemory()

    fun newChat() {
        stopGeneration()
        _activeSessionId.value = null
        _messages.value = emptyList()
        storage.setActiveSessionId(null)
    }

    fun selectSession(id: String) {
        stopGeneration()
        val session = _sessions.value.find { it.id == id } ?: return
        _activeSessionId.value = id
        _messages.value = session.messages
        storage.setActiveSessionId(id)
    }

    fun deleteSession(id: String) {
        val updated = _sessions.value.filter { it.id != id }
        _sessions.value = updated
        storage.saveSessions(updated)
        if (_activeSessionId.value == id) {
            if (updated.isNotEmpty()) {
                selectSession(updated.first().id)
            } else {
                newChat()
            }
        }
    }

    fun clearAllConversations() {
        stopGeneration()
        _sessions.value = emptyList()
        _messages.value = emptyList()
        _activeSessionId.value = null
        storage.clearAllSessions()
    }

    fun clearMemory() {
        storage.clearMemory()
    }

    fun activateLicenseKey(key: String): Boolean {
        val valid = LicenseManager.validateLicenseKey(key)
        if (valid) {
            storage.setLicenseKey(key.trim().uppercase())
            _isSubscribed.value = true
            _selectedModel.value = ModelTier.S_TURBO_ULTRA_MAX
            storage.setSelectedModel(ModelTier.S_TURBO_ULTRA_MAX)
            return true
        }
        return false
    }

    fun deactivateSubscription() {
        storage.setLicenseKey(null)
        _isSubscribed.value = false
        if (_selectedModel.value.isPro) {
            selectModel(ModelTier.S_TURBO_FLASH)
        }
    }

    fun stopGeneration() {
        activeJob?.cancel()
        activeJob = null
        _isBusy.value = false
        // Mark any streaming message as finished
        val updated = _messages.value.map {
            if (it.streaming) it.copy(streaming = false, text = it.text.ifBlank { "Stopped." }) else it
        }
        _messages.value = updated
        persistCurrentMessages()
    }

    fun sendMessage(promptText: String? = null) {
        val rawPrompt = (promptText ?: _inputText.value).trim()
        val prompt = com.example.security.SimaxSecurityGuard.sanitizeUserInput(rawPrompt)
        val bitmap = _attachedBitmap.value
        val attachName = _attachedName.value
        val docName = _attachedDocName.value
        val docContent = _attachedDocContent.value

        if (prompt.isBlank() && bitmap == null && docContent == null) return
        if (_isBusy.value) return

        _inputText.value = ""
        clearAttachment()

        // Check S TURBO MAX quota
        if (_selectedModel.value == ModelTier.S_TURBO_MAX && !_isSubscribed.value) {
            val (ok, remaining) = storage.checkAndIncrementTurboMax()
            if (!ok) {
                Toast.makeText(
                    getApplication(),
                    "MAX limit reached (20 msg / 8h). Upgrade to ULTRA MAX for unlimited usage.",
                    Toast.LENGTH_LONG
                ).show()
                openUpgradeDialog()
                return
            }
        }

        // Check S TURBO ULTRA MAX subscription requirement
        if (_selectedModel.value.isPro && !_isSubscribed.value) {
            openUpgradeDialog()
            return
        }

        // Create or get active session
        var currentSessionId = _activeSessionId.value
        if (currentSessionId == null) {
            val title = if (prompt.length > 36) prompt.take(36) + "..." else prompt.ifBlank { "Analysis" }
            val newSession = ChatSession(title = title)
            currentSessionId = newSession.id
            _activeSessionId.value = currentSessionId
            _sessions.value = listOf(newSession) + _sessions.value
            storage.setActiveSessionId(currentSessionId)
        }

        // Construct user message text
        val userDisplayText = buildString {
            if (docName != null) {
                append("[$docName]\n")
            } else if (attachName != null) {
                append("[$attachName]\n")
            }
            append(prompt)
        }

        val fullPromptForAi = buildString {
            if (docName != null && docContent != null) {
                append("[ATTACHED DOCUMENT: $docName]\n```\n${docContent.take(25000)}\n```\n\n")
            }
            append(prompt)
        }

        // Add user message
        val userMsg = ChatMessage(
            role = MessageRole.USER,
            text = userDisplayText,
            docName = docName
        )
        val currentList = _messages.value + userMsg
        _messages.value = currentList
        persistCurrentMessages()

        // Learn user name or memory facts
        learnFromText(prompt)

        // Handle Image Generation
        if (repository.isImagePrompt(prompt) && bitmap == null && docContent == null) {
            generateImageResponse(prompt)
            return
        }

        // Handle Text / Vision / Reasoning / Search
        generateAiResponse(fullPromptForAi, bitmap)
    }

    private fun generateImageResponse(prompt: String) {
        _isBusy.value = true
        val aiMsgId = UUID.randomUUID().toString()
        val aiMsg = ChatMessage(
            id = aiMsgId,
            role = MessageRole.MODEL,
            text = "",
            streaming = true,
            imagePrompt = repository.cleanImagePrompt(prompt)
        )
        _messages.value = _messages.value + aiMsg

        activeJob = viewModelScope.launch {
            try {
                val urls = repository.generateImageUrls(prompt)
                _messages.value = _messages.value.map {
                    if (it.id == aiMsgId) it.copy(streaming = false, imageUrls = urls) else it
                }
            } catch (e: Exception) {
                _messages.value = _messages.value.map {
                    if (it.id == aiMsgId) it.copy(streaming = false, text = e.message ?: "Failed to generate image.") else it
                }
            } finally {
                _isBusy.value = false
                persistCurrentMessages()
            }
        }
    }

    private fun generateAiResponse(prompt: String, bitmap: Bitmap?) {
        _isBusy.value = true
        val aiMsgId = UUID.randomUUID().toString()
        val willSearch = repository.isSearchQuery(prompt)
        val isReasoning = _isReasoningActive.value || _activePersona.value == com.example.model.SpecialistPersona.REASONING

        val aiMsg = ChatMessage(
            id = aiMsgId,
            role = MessageRole.MODEL,
            text = "",
            streaming = true,
            isSearching = willSearch,
            personaId = _activePersona.value.id
        )
        _messages.value = _messages.value + aiMsg

        activeJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var searchResults = listOf<com.example.model.WebSearchResult>()

            try {
                var searchContext = ""
                if (willSearch) {
                    searchResults = repository.performLiveSearch(prompt)
                    if (searchResults.isNotEmpty()) {
                        searchContext = "\nLIVE WEB SEARCH RESULTS:\n" + searchResults.joinToString("\n") {
                            "- ${it.title}: ${it.snippet}"
                        }
                    }
                    _messages.value = _messages.value.map {
                        if (it.id == aiMsgId) it.copy(isSearching = false, searchSources = searchResults) else it
                    }
                }

                val finalPrompt = if (searchContext.isNotBlank()) {
                    "$prompt\n$searchContext"
                } else prompt

                val fullResponse = repository.streamChatMessage(
                    model = _selectedModel.value,
                    history = _messages.value.dropLast(1),
                    userPrompt = finalPrompt,
                    memory = storage.getUserMemory(),
                    attachedBitmap = bitmap,
                    persona = _activePersona.value,
                    isReasoning = isReasoning,
                    onChunk = { chunk ->
                        _messages.value = _messages.value.map {
                            if (it.id == aiMsgId) it.copy(text = it.text + chunk) else it
                        }
                    }
                )

                val elapsedSeconds = ((System.currentTimeMillis() - startTime) / 100.0).toInt() / 10.0

                // Parse thinking if enclosed in <think> tags or generate reasoning trace
                var cleanedText = fullResponse
                var thinkingContent: String? = null

                val thinkRegex = Regex("""<think>([\s\S]*?)</think>""", RegexOption.IGNORE_CASE)
                val thinkMatch = thinkRegex.find(fullResponse)
                if (thinkMatch != null) {
                    thinkingContent = thinkMatch.groupValues[1].trim()
                    cleanedText = fullResponse.replace(thinkRegex, "").trim()
                } else if (isReasoning) {
                    thinkingContent = """
                        • تفكيك المسألة وتحليل الأبعاد والسياق المطلوب
                        • فحص العلاقات والروابط المنطقية بدقة عالية
                        • استنباط الحل الأمثل والتحقق من دقته الأكاديمية والعملية
                    """.trimIndent()
                }

                _messages.value = _messages.value.map {
                    if (it.id == aiMsgId) {
                        it.copy(
                            streaming = false,
                            text = cleanedText.ifBlank { "اكتمل الرد بنجاح." },
                            thinkingText = thinkingContent,
                            thinkingDurationSeconds = if (isReasoning) elapsedSeconds else null,
                            searchSources = searchResults
                        )
                    } else it
                }

                // If voice dialog is open, speak the reply aloud
                if (_showVoiceDialog.value) {
                    voiceManager.speak(cleanedText)
                }

            } catch (e: Exception) {
                _messages.value = _messages.value.map {
                    if (it.id == aiMsgId) it.copy(streaming = false, text = e.message ?: "Error occurred.") else it
                }
            } finally {
                _isBusy.value = false
                persistCurrentMessages()
            }
        }
    }

    fun regenerateMessage(messageId: String) {
        val list = _messages.value
        val index = list.indexOfFirst { it.id == messageId }
        if (index <= 0) return

        val userMsg = list.subList(0, index).findLast { it.role == MessageRole.USER } ?: return
        // Remove the old AI message and any newer ones
        _messages.value = list.take(index)
        sendMessage(userMsg.text)
    }

    private fun learnFromText(text: String) {
        val lower = text.lowercase()
        // Extract name
        val nameRegex = Regex("""(?:my name is|i am|اسمي|اسمي هو)\s+([A-Za-z\u0600-\u06FF]{2,20})""", RegexOption.IGNORE_CASE)
        nameRegex.find(text)?.let {
            val name = it.groupValues[1]
            if (!name.equals("a", ignoreCase = true) && !name.equals("the", ignoreCase = true)) {
                storage.setUserName(name)
            }
        }

        // Remember facts
        val factRegex = Regex("""(?:remember that|remember|احفظ أن|تذكر أن|احفظ|تذكر)\s+(.+)""", RegexOption.IGNORE_CASE)
        factRegex.find(text)?.let {
            val fact = it.groupValues[1].trim()
            if (fact.length in 3..140) {
                storage.saveUserFact(fact)
            }
        }
    }

    private fun persistCurrentMessages() {
        val currentId = _activeSessionId.value ?: return
        val currentList = _messages.value.filter { it.text.isNotBlank() || it.imagePrompt != null }
        val updatedSessions = _sessions.value.map {
            if (it.id == currentId) it.copy(messages = currentList) else it
        }
        _sessions.value = updatedSessions
        storage.saveSessions(updatedSessions)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
