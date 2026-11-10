package com.proov.proov.core.crypto

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.ProviderException
import java.security.UnrecoverableKeyException

/** 암호문을 [dir] 아래에 키별 파일로 저장한다. 읽기와 쓰기는 하나의 락으로 직렬화한다. */
internal class FileSecureStorage(private val dir: File, private val keys: AesKeys) : SecureStorage {
    private val mutex = Mutex()

    override suspend fun put(key: SecureKey, value: String): Result<Unit> = locked {
        runCatching { write(key, value) }
            .onFailure { Logger.e("보안 저장소 저장 실패(${key.id}): ${it::class.simpleName}") }
    }

    override suspend fun get(key: SecureKey): String? = locked { read(key) }

    override suspend fun remove(key: SecureKey): Result<Unit> = locked {
        runCatching { delete(key) }
    }

    private suspend fun <T> locked(block: () -> T): T = withContext(Dispatchers.IO) { mutex.withLock { block() } }

    /** 키 관련 예외일 때만 키를 다시 만들어 1회 재시도한다. 그 외의 예외는 키와 무관하므로 키를 건드리지 않는다. */
    private fun write(key: SecureKey, value: String) {
        val encrypted = try {
            AesGcmCodec.encrypt(keys.getOrCreate(), value, key.id)
        } catch (e: Exception) {
            if (!e.isKeyFailure()) throw e
            Logger.w("암호화 키 접근 실패(${e::class.simpleName}). 키 재생성 후 1회 재시도")
            // 새 키로는 기존 암호문을 읽을 수 없으므로 키와 저장된 항목을 함께 지운다
            keys.reset()
            dir.listFiles()?.forEach { it.delete() }
            AesGcmCodec.encrypt(keys.getOrCreate(), value, key.id)
        }
        writeAtomically(key, encrypted)
    }

    private fun read(key: SecureKey): String? {
        val file = file(key)
        if (!file.exists()) return null
        // 읽기 IOException은 일시 오류로 보고 삭제하지 않고 전파한다
        val data = file.readBytes()
        return try {
            val secret = keys.get() ?: throw KeyStoreException("저장된 암호문에 대응하는 키가 없다")
            AesGcmCodec.decrypt(secret, data, key.id)
        } catch (e: Exception) {
            if (!e.isPermanent()) throw e
            Logger.w("복호화 영구 실패(${key.id}): ${e::class.simpleName}. 항목을 삭제")
            delete(key)
            null
        }
    }

    private fun delete(key: SecureKey) {
        val file = file(key)
        if (file.exists() && !file.delete()) throw IOException("보안 저장소 항목을 삭제하지 못했다: ${key.id}")
    }

    /** 임시 파일에 쓴 뒤 이름을 바꿔, 쓰는 도중 중단되어도 기존 파일이 손상되지 않게 한다. */
    private fun writeAtomically(key: SecureKey, bytes: ByteArray) {
        dir.mkdirs()
        val target = file(key)
        val temp = File(dir, "${key.id}.tmp")
        temp.writeBytes(bytes)
        // Android(POSIX)에서는 첫 renameTo가 대상을 원자적으로 교체한다. 대상이 있으면 실패하는 환경(Windows 호스트 테스트)용 대비이다
        if (!temp.renameTo(target)) {
            target.delete()
            if (!temp.renameTo(target)) throw IOException("보안 저장소 파일을 쓰지 못했다: ${key.id}")
        }
    }

    private fun file(key: SecureKey) = File(dir, key.id)

    private fun Exception.isKeyFailure() = this is KeyStoreException ||
        this is ProviderException ||
        this is InvalidKeyException ||
        this is UnrecoverableKeyException

    /** 다시 시도해도 복구되지 않는 실패: 인증 실패, 변조, 형식 오류, 키 손상 */
    private fun Exception.isPermanent() = this is GeneralSecurityException || this is ProviderException
}
