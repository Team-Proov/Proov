package com.proov.proov.core.crypto

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IosSecureStorageTest {
    private val storage = IosSecureStorage()

    @Test
    fun 저장_덮어쓰기_삭제가_동작한다() = runBlocking {
        storage.remove(SecureKey.ACCESS_TOKEN)
        assertNull(storage.get(SecureKey.ACCESS_TOKEN))

        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "old").isSuccess)
        assertTrue(storage.put(SecureKey.ACCESS_TOKEN, "new").isSuccess)
        assertEquals("new", storage.get(SecureKey.ACCESS_TOKEN))

        assertTrue(storage.remove(SecureKey.ACCESS_TOKEN).isSuccess)
        assertNull(storage.get(SecureKey.ACCESS_TOKEN))
    }
}
