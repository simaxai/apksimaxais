package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ChatMessage
import com.example.model.ChatSession
import com.example.model.MessageRole
import com.example.model.ModelTier
import com.example.model.UserMemory
import org.json.JSONArray
import org.json.JSONObject

class StorageManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("simax_storage_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SESSIONS = "simax_sessions"
        private const val KEY_ACTIVE_ID = "simax_active_session_id"
        private const val KEY_SELECTED_MODEL = "simax_selected_model"
        private const val KEY_IS_DARK = "simax_is_dark_theme"
        private const val KEY_LICENSE_KEY = "simax_license_key"
        private const val KEY_MAX_COUNT = "simax_turbo_max_count"
        private const val KEY_MAX_COOLDOWN = "simax_turbo_max_cooldown"
        private const val KEY_USER_NAME = "simax_user_name"
        private const val KEY_ABOUT_USER = "simax_about_user"
        private const val KEY_RESPONSE_INSTRUCTIONS = "simax_response_instructions"
        private const val KEY_ACTIVE_PERSONA = "simax_active_persona"
        private const val KEY_USER_FACTS = "simax_user_facts"
        private const val KEY_USER_PREFS = "simax_user_preferences"
        private const val KEY_GOOGLE_LOGGED_IN = "simax_google_logged_in"
        private const val KEY_GOOGLE_EMAIL = "simax_google_email"
    }

    fun getGoogleAccount(): Pair<Boolean, String> {
        val isLoggedIn = prefs.getBoolean(KEY_GOOGLE_LOGGED_IN, true)
        val email = prefs.getString(KEY_GOOGLE_EMAIL, "simaxx991top@gmail.com") ?: "simaxx991top@gmail.com"
        return Pair(isLoggedIn, email)
    }

    fun setGoogleAccount(isLoggedIn: Boolean, email: String = "simaxx991top@gmail.com") {
        prefs.edit()
            .putBoolean(KEY_GOOGLE_LOGGED_IN, isLoggedIn)
            .putString(KEY_GOOGLE_EMAIL, email)
            .apply()
    }

    fun isDarkTheme(): Boolean = prefs.getBoolean(KEY_IS_DARK, true)

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }

    fun getSelectedModel(): ModelTier {
        val id = prefs.getString(KEY_SELECTED_MODEL, ModelTier.S_TURBO_FLASH.id) ?: ModelTier.S_TURBO_FLASH.id
        return ModelTier.fromId(id)
    }

    fun setSelectedModel(model: ModelTier) {
        prefs.edit().putString(KEY_SELECTED_MODEL, model.id).apply()
    }

    fun getActivePersona(): String = prefs.getString(KEY_ACTIVE_PERSONA, "general") ?: "general"

    fun setActivePersona(personaId: String) {
        prefs.edit().putString(KEY_ACTIVE_PERSONA, personaId).apply()
    }

    fun getLicenseKey(): String? = prefs.getString(KEY_LICENSE_KEY, null)

    fun setLicenseKey(key: String?) {
        prefs.edit().putString(KEY_LICENSE_KEY, key).apply()
    }

    fun isSubscribed(): Boolean {
        val key = getLicenseKey() ?: return false
        return LicenseManager.validateLicenseKey(key)
    }

    fun checkAndIncrementTurboMax(): Pair<Boolean, Int> {
        val now = System.currentTimeMillis()
        val cooldown = prefs.getLong(KEY_MAX_COOLDOWN, 0L)
        if (cooldown > 0 && now < cooldown) {
            return Pair(false, 0)
        }
        var count = prefs.getInt(KEY_MAX_COUNT, 0)
        if (cooldown > 0 && now >= cooldown) {
            count = 0
            prefs.edit().putLong(KEY_MAX_COOLDOWN, 0L).apply()
        }
        if (count >= 20) {
            val nextCooldown = now + (8L * 3600L * 1000L)
            prefs.edit().putLong(KEY_MAX_COOLDOWN, nextCooldown).apply()
            return Pair(false, 0)
        }
        count += 1
        val remaining = 20 - count
        val editor = prefs.edit().putInt(KEY_MAX_COUNT, count)
        if (remaining <= 0) {
            editor.putLong(KEY_MAX_COOLDOWN, now + (8L * 3600L * 1000L))
        }
        editor.apply()
        return Pair(true, remaining)
    }

    fun getUserMemory(): UserMemory {
        val name = prefs.getString(KEY_USER_NAME, null)
        val aboutUser = prefs.getString(KEY_ABOUT_USER, null)
        val responseInstructions = prefs.getString(KEY_RESPONSE_INSTRUCTIONS, null)
        val factsJson = prefs.getString(KEY_USER_FACTS, "[]") ?: "[]"
        val facts = mutableListOf<String>()
        val factsArr = JSONArray(factsJson)
        for (i in 0 until factsArr.length()) {
            facts.add(factsArr.getString(i))
        }

        val prefsJson = prefs.getString(KEY_USER_PREFS, "[]") ?: "[]"
        val userPrefs = mutableListOf<String>()
        val prefsArr = JSONArray(prefsJson)
        for (i in 0 until prefsArr.length()) {
            userPrefs.add(prefsArr.getString(i))
        }
        return UserMemory(
            name = name,
            aboutUser = aboutUser,
            responseInstructions = responseInstructions,
            facts = facts,
            preferences = userPrefs
        )
    }

    fun setCustomInstructions(aboutUser: String?, responseInstructions: String?) {
        prefs.edit()
            .putString(KEY_ABOUT_USER, aboutUser?.trim())
            .putString(KEY_RESPONSE_INSTRUCTIONS, responseInstructions?.trim())
            .apply()
    }

    fun saveUserFact(fact: String) {
        val memory = getUserMemory()
        if (memory.facts.any { it.equals(fact, ignoreCase = true) }) return
        val updated = (memory.facts + fact.trim()).takeLast(40)
        val arr = JSONArray(updated)
        prefs.edit().putString(KEY_USER_FACTS, arr.toString()).apply()
    }

    fun removeUserFact(fact: String) {
        val memory = getUserMemory()
        val updated = memory.facts.filterNot { it.equals(fact, ignoreCase = true) }
        val arr = JSONArray(updated)
        prefs.edit().putString(KEY_USER_FACTS, arr.toString()).apply()
    }

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name.trim()).apply()
    }

    fun clearMemory() {
        prefs.edit()
            .remove(KEY_USER_NAME)
            .remove(KEY_ABOUT_USER)
            .remove(KEY_RESPONSE_INSTRUCTIONS)
            .remove(KEY_USER_FACTS)
            .remove(KEY_USER_PREFS)
            .apply()
    }

    fun getActiveSessionId(): String? = prefs.getString(KEY_ACTIVE_ID, null)

    fun setActiveSessionId(id: String?) {
        prefs.edit().putString(KEY_ACTIVE_ID, id).apply()
    }

    fun loadSessions(): List<ChatSession> {
        val jsonStr = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
        return try {
            val list = mutableListOf<ChatSession>()
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.getString("id")
                val title = obj.getString("title")
                val createdAt = obj.getLong("createdAt")
                val msgArr = obj.getJSONArray("messages")
                val messages = mutableListOf<ChatMessage>()
                for (j in 0 until msgArr.length()) {
                    val mObj = msgArr.getJSONObject(j)
                    val mId = mObj.getString("id")
                    val roleStr = mObj.getString("role")
                    val role = if (roleStr == "USER") MessageRole.USER else MessageRole.MODEL
                    val text = mObj.getString("text")
                    val timestamp = mObj.optLong("timestamp", System.currentTimeMillis())
                    val imagePrompt = if (mObj.has("imagePrompt")) mObj.getString("imagePrompt") else null
                    val imageUrls = mutableListOf<String>()
                    if (mObj.has("imageUrls")) {
                        val imgArr = mObj.getJSONArray("imageUrls")
                        for (k in 0 until imgArr.length()) {
                            imageUrls.add(imgArr.getString(k))
                        }
                    }
                    val mediaUri = if (mObj.has("mediaDataUri")) mObj.getString("mediaDataUri") else null
                    val mediaType = if (mObj.has("mediaType")) mObj.getString("mediaType") else null
                    val mediaName = if (mObj.has("mediaName")) mObj.getString("mediaName") else null
                    val thinkingText = if (mObj.has("thinkingText")) mObj.getString("thinkingText") else null
                    val thinkingDuration = if (mObj.has("thinkingDuration")) mObj.getDouble("thinkingDuration") else null
                    val personaId = if (mObj.has("personaId")) mObj.getString("personaId") else null
                    val docName = if (mObj.has("docName")) mObj.getString("docName") else null

                    val searchSources = mutableListOf<com.example.model.WebSearchResult>()
                    if (mObj.has("searchSources")) {
                        val sArr = mObj.getJSONArray("searchSources")
                        for (k in 0 until sArr.length()) {
                            val sItem = sArr.getJSONObject(k)
                            searchSources.add(
                                com.example.model.WebSearchResult(
                                    title = sItem.optString("title", ""),
                                    snippet = sItem.optString("snippet", ""),
                                    url = sItem.optString("url", "")
                                )
                            )
                        }
                    }

                    messages.add(
                        ChatMessage(
                            id = mId,
                            role = role,
                            text = text,
                            streaming = false,
                            timestamp = timestamp,
                            imagePrompt = imagePrompt,
                            imageUrls = imageUrls,
                            mediaDataUri = mediaUri,
                            mediaType = mediaType,
                            mediaName = mediaName,
                            thinkingText = thinkingText,
                            thinkingDurationSeconds = thinkingDuration,
                            searchSources = searchSources,
                            personaId = personaId,
                            docName = docName
                        )
                    )
                }
                list.add(ChatSession(id = id, title = title, createdAt = createdAt, messages = messages))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveSessions(sessions: List<ChatSession>) {
        try {
            val arr = JSONArray()
            for (session in sessions) {
                val sObj = JSONObject()
                sObj.put("id", session.id)
                sObj.put("title", session.title)
                sObj.put("createdAt", session.createdAt)
                val mArr = JSONArray()
                for (msg in session.messages) {
                    val mObj = JSONObject()
                    mObj.put("id", msg.id)
                    mObj.put("role", msg.role.name)
                    mObj.put("text", msg.text)
                    mObj.put("timestamp", msg.timestamp)
                    msg.imagePrompt?.let { mObj.put("imagePrompt", it) }
                    if (msg.imageUrls.isNotEmpty()) {
                        mObj.put("imageUrls", JSONArray(msg.imageUrls))
                    }
                    msg.mediaDataUri?.let { mObj.put("mediaDataUri", it) }
                    msg.mediaType?.let { mObj.put("mediaType", it) }
                    msg.mediaName?.let { mObj.put("mediaName", it) }
                    msg.thinkingText?.let { mObj.put("thinkingText", it) }
                    msg.thinkingDurationSeconds?.let { mObj.put("thinkingDuration", it) }
                    msg.personaId?.let { mObj.put("personaId", it) }
                    msg.docName?.let { mObj.put("docName", it) }
                    if (msg.searchSources.isNotEmpty()) {
                        val sArr = JSONArray()
                        for (s in msg.searchSources) {
                            val so = JSONObject()
                            so.put("title", s.title)
                            so.put("snippet", s.snippet)
                            so.put("url", s.url)
                            sArr.put(so)
                        }
                        mObj.put("searchSources", sArr)
                    }
                    mArr.put(mObj)
                }
                sObj.put("messages", mArr)
                arr.put(sObj)
            }
            prefs.edit().putString(KEY_SESSIONS, arr.toString()).apply()
        } catch (_: Exception) {}
    }

    fun clearAllSessions() {
        prefs.edit().remove(KEY_SESSIONS).remove(KEY_ACTIVE_ID).apply()
    }
}
