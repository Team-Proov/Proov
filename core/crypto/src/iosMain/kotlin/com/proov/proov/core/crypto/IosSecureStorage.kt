package com.proov.proov.core.crypto

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecInteractionNotAllowed
import platform.Security.errSecItemNotFound
import platform.Security.errSecNotAvailable
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

/**
 * Keychain에 값을 저장한다. 암호화는 OS가 하므로 앱은 키를 다루지 않는다.
 *
 * - 접근 조건은 `AfterFirstUnlockThisDeviceOnly`이다. 기기가 잠긴 상태의 백그라운드 접근을 허용하고,
 *   iCloud 동기화와 백업을 통한 다른 기기 이전은 막는다.
 * - Keychain 항목은 앱을 삭제해도 남으므로, 설치마다 처음 접근할 때 이전 항목을 모두 지운다.
 *   `NSUserDefaults`는 앱 삭제와 함께 사라지므로 설치 표시로 쓴다.
 */
@OptIn(ExperimentalForeignApi::class)
@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class IosSecureStorage : SecureStorage {
    private val mutex = Mutex()

    private val installChecked: Unit by lazy {
        val defaults = NSUserDefaults.standardUserDefaults
        if (!defaults.boolForKey(INSTALL_FLAG)) {
            withAttrs({
                putRef(kSecClass, kSecClassGenericPassword)
                putObject(kSecAttrService, SERVICE)
            }) { SecItemDelete(it) }
            defaults.setBool(true, INSTALL_FLAG)
        }
    }

    override suspend fun put(key: SecureKey, value: String): Result<Unit> = locked {
        runCatching { write(key, value) }
            .onFailure { Logger.e("보안 저장소 저장 실패(${key.id}): ${it.message}") }
    }

    override suspend fun get(key: SecureKey): String? = locked { read(key) }

    override suspend fun remove(key: SecureKey): Result<Unit> = locked { runCatching { delete(key) } }

    private suspend fun <T> locked(block: () -> T): T =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                installChecked
                block()
            }
        }

    private fun write(key: SecureKey, value: String) {
        val data = value.encodeToByteArray().toNSData()
        val updated = withAttrs({ identify(key) }) { query ->
            withAttrs({ putObject(kSecValueData, data) }) { changes -> SecItemUpdate(query, changes) }
        }
        if (updated == errSecSuccess) return
        check(updated == errSecItemNotFound) { "Keychain 갱신 실패: $updated" }

        val added = withAttrs({
            identify(key)
            putObject(kSecValueData, data)
            putRef(kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
        }) { SecItemAdd(it, null) }
        check(added == errSecSuccess) { "Keychain 추가 실패: $added" }
    }

    private fun read(key: SecureKey): String? = memScoped {
        val result = alloc<CFTypeRefVar>()
        val status = withAttrs({
            identify(key)
            putRef(kSecReturnData, kCFBooleanTrue)
            putRef(kSecMatchLimit, kSecMatchLimitOne)
        }) { SecItemCopyMatching(it, result.ptr) }

        when (status) {
            errSecSuccess -> (CFBridgingRelease(result.value) as? NSData)?.toByteArray()?.decodeToString()
            errSecItemNotFound -> null
            // 기기가 잠겼거나 Keychain이 아직 준비되지 않은 일시 상태이므로 삭제하지 않고 전파한다
            errSecInteractionNotAllowed, errSecNotAvailable ->
                throw IllegalStateException("Keychain을 일시적으로 사용할 수 없다: $status")
            else -> {
                Logger.w("Keychain 조회 영구 실패(${key.id}): $status. 항목을 삭제")
                delete(key)
                null
            }
        }
    }

    private fun delete(key: SecureKey) {
        val status = withAttrs({ identify(key) }) { SecItemDelete(it) }
        check(status == errSecSuccess || status == errSecItemNotFound) { "Keychain 삭제 실패: $status" }
    }

    private fun Attrs.identify(key: SecureKey) {
        putRef(kSecClass, kSecClassGenericPassword)
        putObject(kSecAttrService, SERVICE)
        putObject(kSecAttrAccount, key.id)
    }

    /** CFDictionary 조립과 해제를 한 곳에 모은다. 값으로 넣은 Kotlin 객체는 retain 했다가 함께 release 한다. */
    private class Attrs {
        val dict = CFDictionaryCreateMutable(null, 0, null, null)
        private val retained = mutableListOf<CFTypeRef?>()

        fun putRef(key: CFStringRef?, value: CFTypeRef?) = CFDictionaryAddValue(dict, key, value)

        fun putObject(key: CFStringRef?, value: Any) {
            val ref = CFBridgingRetain(value)
            retained += ref
            CFDictionaryAddValue(dict, key, ref)
        }

        fun release() {
            retained.forEach { CFRelease(it) }
            CFRelease(dict)
        }
    }

    private inline fun <T> withAttrs(build: Attrs.() -> Unit, block: (CFDictionaryRef?) -> T): T {
        val attrs = Attrs().apply(build)
        try {
            return block(attrs.dict)
        } finally {
            attrs.release()
        }
    }

    private fun ByteArray.toNSData(): NSData =
        if (isEmpty()) NSData() else usePinned { NSData.create(bytes = it.addressOf(0), length = size.convert()) }

    private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).also { bytes ->
        if (bytes.isNotEmpty()) bytes.usePinned { memcpy(it.addressOf(0), this.bytes, length) }
    }

    private companion object {
        const val SERVICE = "com.proov.proov.crypto"
        const val INSTALL_FLAG = "com.proov.proov.crypto.installed"
    }
}
