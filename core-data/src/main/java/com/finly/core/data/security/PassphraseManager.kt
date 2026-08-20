package com.finly.core.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class PassphraseManager(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "moneymind_db_passphrase_key"
        private const val PREFS_NAME = "moneymind_secure_prefs"
        private const val ENCRYPTED_PASSPHRASE_KEY = "encrypted_passphrase"
        private const val IV_KEY = "passphrase_iv"
    }

    fun getOrGeneratePassphrase(): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedPassphrase = prefs.getString(ENCRYPTED_PASSPHRASE_KEY, null)
        val ivString = prefs.getString(IV_KEY, null)

        if (encryptedPassphrase != null && ivString != null) {
            val iv = android.util.Base64.decode(ivString, android.util.Base64.DEFAULT)
            val encryptedBytes = android.util.Base64.decode(encryptedPassphrase, android.util.Base64.DEFAULT)
            return decryptPassphrase(encryptedBytes, iv)
        }

        val rawPassphrase = ByteArray(32)
        java.security.SecureRandom().nextBytes(rawPassphrase)

        val (encryptedBytes, iv) = encryptPassphrase(rawPassphrase)
        prefs.edit()
            .putString(ENCRYPTED_PASSPHRASE_KEY, android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT))
            .putString(IV_KEY, android.util.Base64.encodeToString(iv, android.util.Base64.DEFAULT))
            .apply()

        return rawPassphrase
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(keyGenSpec)
            return keyGenerator.generateKey()
        }
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun encryptPassphrase(rawPassphrase: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val encrypted = cipher.doFinal(rawPassphrase)
        return Pair(encrypted, cipher.iv)
    }

    private fun decryptPassphrase(encryptedPassphrase: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return cipher.doFinal(encryptedPassphrase)
    }
}
