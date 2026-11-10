package com.proov.proov.core.crypto

import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import java.security.GeneralSecurityException
import java.security.KeyStoreException
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Keystore는 JVM에서 쓸 수 없으므로 일반 AES 키를 주입해 포맷, AAD, 복구 정책을 검증한다. */
class FileSecureStorageTest {
    private class FakeKeys(var secret: SecretKey = newKey()) : AesKeys {
        var present = true
        var failures = 0
        var failure: Exception = KeyStoreException("테스트 실패")
        var resetCount = 0

        override fun get(): SecretKey? = if (present) secret else null

        override fun getOrCreate(): SecretKey {
            if (failures > 0) {
                failures--
                throw failure
            }
            present = true
            return secret
        }

        override fun reset() {
            resetCount++
            present = false
            secret = newKey()
        }
    }

    private val dir: File = Files.createTempDirectory("secure").toFile().also { it.deleteOnExit() }
    private val keys = FakeKeys()
    private val storage = FileSecureStorage(dir, keys)

    @Test
    fun 저장한_값을_그대로_읽는다() = runBlocking {
        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "access").isSuccess)
        assertEquals("access", storage.get(SecureKey.ACCESS_TOKEN))
        assertNull(storage.get(SecureKey.REFRESH_TOKEN))
    }

    @Test
    fun 평문이_파일에_남지_않는다() = runBlocking {
        storage.put(SecureKey.ACCESS_TOKEN, "plain-token-value")
        assertFalse(File(dir, "access_token").readBytes().decodeToString().contains("plain-token-value"))
    }

    @Test
    fun 다른_용도_자리로_옮긴_암호문은_읽지_못하고_삭제된다() = runBlocking {
        storage.put(SecureKey.ACCESS_TOKEN, "access")
        File(dir, "access_token").copyTo(File(dir, "refresh_token"))

        assertNull(storage.get(SecureKey.REFRESH_TOKEN))
        assertFalse(File(dir, "refresh_token").exists())
        assertEquals("access", storage.get(SecureKey.ACCESS_TOKEN))
    }

    @Test
    fun 변조된_암호문은_삭제하고_null을_반환한다() = runBlocking {
        storage.put(SecureKey.ACCESS_TOKEN, "access")
        val file = File(dir, "access_token")
        file.writeBytes(file.readBytes().also { it[it.lastIndex] = (it.last() + 1).toByte() })

        assertNull(storage.get(SecureKey.ACCESS_TOKEN))
        assertFalse(file.exists())
    }

    @Test
    fun 형식이_잘못된_파일은_삭제하고_null을_반환한다() = runBlocking {
        File(dir, "access_token").writeBytes(byteArrayOf(1, 2, 3))

        assertNull(storage.get(SecureKey.ACCESS_TOKEN))
        assertFalse(File(dir, "access_token").exists())
    }

    @Test
    fun 키가_사라지면_복호화_영구_실패로_보고_삭제한다() = runBlocking {
        storage.put(SecureKey.ACCESS_TOKEN, "access")
        keys.present = false

        assertNull(storage.get(SecureKey.ACCESS_TOKEN))
        assertFalse(File(dir, "access_token").exists())
    }

    @Test
    fun 키_접근이_실패하면_키를_재생성하고_저장된_다른_항목도_지운_뒤_재시도한다() = runBlocking {
        storage.put(SecureKey.REFRESH_TOKEN, "refresh")
        keys.failures = 1

        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "access").isSuccess)

        assertEquals(1, keys.resetCount)
        assertEquals("access", storage.get(SecureKey.ACCESS_TOKEN))
        assertNull(storage.get(SecureKey.REFRESH_TOKEN))
    }

    @Test
    fun 재시도도_실패하면_실패_결과를_반환한다() = runBlocking {
        keys.failures = 2

        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "access").isFailure)
        assertEquals(1, keys.resetCount)
    }

    @Test
    fun 키와_무관한_예외는_키를_재생성하지_않는다() = runBlocking {
        storage.put(SecureKey.REFRESH_TOKEN, "refresh")
        keys.failures = 1
        keys.failure = IllegalStateException("키와 무관한 오류")

        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "access").isFailure)
        assertEquals(0, keys.resetCount)
        assertEquals("refresh", storage.get(SecureKey.REFRESH_TOKEN))
    }

    @Test
    fun 덮어쓰기와_삭제가_동작한다() = runBlocking {
        storage.put(SecureKey.ACCESS_TOKEN, "old")
        storage.put(SecureKey.ACCESS_TOKEN, "new")
        assertEquals("new", storage.get(SecureKey.ACCESS_TOKEN))

        assertTrue(storage.remove(SecureKey.ACCESS_TOKEN).isSuccess)
        assertNull(storage.get(SecureKey.ACCESS_TOKEN))
        assertTrue(storage.remove(SecureKey.ACCESS_TOKEN).isSuccess)
    }

    @Test
    fun 코덱은_AAD가_다르면_인증_단계에서_실패한다() {
        val key = newKey()
        val data = AesGcmCodec.encrypt(key, "value", "access_token")

        assertEquals("value", AesGcmCodec.decrypt(key, data, "access_token"))
        assertFailsWith<GeneralSecurityException> { AesGcmCodec.decrypt(key, data, "refresh_token") }
    }

    private companion object {
        fun newKey(): SecretKey = SecretKeySpec(
            ByteArray(32) {
                (it + 1).toByte()
            }.also { java.security.SecureRandom().nextBytes(it) },
            "AES",
        )
    }
}
