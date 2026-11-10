package com.proov.proov.core.crypto

import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** 형식이 맞지 않는 암호문. 복구할 수 없으므로 영구 실패로 취급한다. */
internal class MalformedCiphertextException : GeneralSecurityException("암호문 형식이 올바르지 않다")

/**
 * AES-256-GCM 암호화. 바이트 배치는 IV 12바이트 + 암호문 + GCM 태그 16바이트이다.
 * [aad]를 인증 데이터로 묶기 때문에 다른 용도의 암호문을 옮겨 넣으면 복호화가 인증 단계에서 실패한다.
 */
internal object AesGcmCodec {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE_BYTES = 12
    private const val TAG_SIZE_BYTES = 16

    fun encrypt(key: SecretKey, plaintext: String, aad: String): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key)
            updateAAD(aad.encodeToByteArray())
        }
        return cipher.iv + cipher.doFinal(plaintext.encodeToByteArray())
    }

    fun decrypt(key: SecretKey, data: ByteArray, aad: String): String {
        if (data.size < IV_SIZE_BYTES + TAG_SIZE_BYTES) throw MalformedCiphertextException()
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BYTES * 8, data, 0, IV_SIZE_BYTES))
            updateAAD(aad.encodeToByteArray())
        }
        return cipher.doFinal(data, IV_SIZE_BYTES, data.size - IV_SIZE_BYTES).decodeToString()
    }
}
