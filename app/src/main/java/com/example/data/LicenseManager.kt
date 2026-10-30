package com.example.data

import android.util.Base64

object LicenseManager {
    private const val SALT = "SIMAX-TEAM-LICENSE-SALT-2026"

    private fun fnv1a(text: String): Long {
        var hash = 2166136261L
        for (ch in text) {
            hash = hash xor ch.code.toLong()
            hash = (hash * 16777619L) and 0xFFFFFFFFL
        }
        return hash
    }

    fun generateLicenseKey(customerId: String, days: Int = 365): String {
        val nowSec = System.currentTimeMillis() / 1000L
        val expirySec = nowSec + (days * 24L * 3600L)
        val payload = "$customerId.$nowSec.$expirySec"
        val hash = fnv1a("$payload.$SALT")
        val hex = java.lang.Long.toHexString(hash).padStart(8, '0').uppercase()
        val combined = "$payload.$hex"

        val b64 = Base64.encodeToString(combined.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            .replace("=", "")
            .replace("+", "-")
            .replace("/", "_")

        val chunked = b64.chunked(4).joinToString("-").uppercase()
        return "SIMAX-$chunked"
    }

    fun validateLicenseKey(rawKey: String, expectedCustomerId: String? = null): Boolean {
        return try {
            val clean = rawKey.trim().uppercase().removePrefix("SIMAX-").replace("-", "")
            val normalized = clean.replace("-", "+").replace("_", "/")
            val padLength = (4 - (normalized.length % 4)) % 4
            val padded = normalized + "=".repeat(padLength)
            val decoded = String(Base64.decode(padded, Base64.NO_WRAP), Charsets.UTF_8)
            val parts = decoded.split(".")
            if (parts.size != 4) return false

            val customerId = parts[0]
            val issuedAt = parts[1].toLongOrNull() ?: return false
            val expiry = parts[2].toLongOrNull() ?: return false
            val signature = parts[3]

            val expectedHash = fnv1a("$customerId.$issuedAt.$expiry.$SALT")
            val expectedHex = java.lang.Long.toHexString(expectedHash).padStart(8, '0').uppercase()

            if (signature != expectedHex) return false
            if (expectedCustomerId != null && customerId != expectedCustomerId) return false

            val currentSec = System.currentTimeMillis() / 1000L
            expiry > currentSec
        } catch (_: Exception) {
            false
        }
    }
}
