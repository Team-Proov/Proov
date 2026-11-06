package com.proov.proov.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/** 암호화 키의 보관처. 호출은 [FileSecureStorage]의 락 안에서만 일어나므로 별도 동기화는 없다. */
internal interface AesKeys {
    fun get(): SecretKey?
    fun getOrCreate(): SecretKey
    fun reset()
}

/** Android Keystore의 AES-256 키. 키는 Keystore 밖으로 나오지 않고 하드웨어 보호 여부는 강제하지 않는다. */
internal class KeystoreAesKeys : AesKeys {
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    override fun get(): SecretKey? = keyStore.getKey(KEY_ALIAS, null) as? SecretKey

    override fun getOrCreate(): SecretKey = get() ?: create()

    override fun reset() {
        if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
    }

    private fun create(): SecretKey =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setKeySize(KEY_SIZE_BITS)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false)
                    .build(),
            )
        }.generateKey()

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "com.proov.proov.crypto.key"
        const val KEY_SIZE_BITS = 256
    }
}
