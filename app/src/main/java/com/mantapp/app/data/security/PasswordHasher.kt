package com.mantapp.app.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject

class PasswordHasher @Inject constructor() {
    fun createHash(password: String): PasswordHash {
        val salt = ByteArray(SALT_BYTE_COUNT)
        secureRandom.nextBytes(salt)
        return PasswordHash(
            hash = hash(password = password, salt = salt),
            salt = Base64.getEncoder().encodeToString(salt),
        )
    }

    fun matches(password: String, expectedHash: String, salt: String): Boolean {
        val saltBytes = Base64.getDecoder().decode(salt)
        return hash(password = password, salt = saltBytes) == expectedHash
    }

    private fun hash(password: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashed = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashed)
    }

    private companion object {
        const val SALT_BYTE_COUNT = 16
        val secureRandom = SecureRandom()
    }
}

data class PasswordHash(
    val hash: String,
    val salt: String,
)
