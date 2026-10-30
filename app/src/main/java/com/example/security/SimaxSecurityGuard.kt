package com.example.security

import android.content.Context
import android.os.Build
import android.util.Log

object SimaxSecurityGuard {
    private const val TAG = "SIMAX-GUARD"
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
        try {
            Log.d(TAG, "SIMAX Guard Protection Engine Active — SIMAX TEAM")
            // Verify package integrity
            val expectedPackage = context.packageName
            if (!expectedPackage.startsWith("com.aistudio") && !expectedPackage.startsWith("com.example")) {
                Log.w(TAG, "Package name mismatch detected: $expectedPackage")
            }
        } catch (_: Exception) {}
    }

    fun isDeviceRootedOrEmulated(): Boolean {
        val buildTags = Build.TAGS
        val isTestKeys = buildTags != null && buildTags.contains("test-keys")
        val isGeneric = Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator")
        return isTestKeys || isGeneric
    }

    fun sanitizeUserInput(input: String): String {
        return input.trim()
            .replace("\u0000", "")
            .replace(Regex("""[\u200B-\u200D\uFEFF]"""), "") // Remove zero-width hidden characters
    }

    fun obscureApiKey(key: String): String {
        if (key.length <= 8) return "••••••••"
        return key.take(4) + "••••••••" + key.takeLast(4)
    }
}
