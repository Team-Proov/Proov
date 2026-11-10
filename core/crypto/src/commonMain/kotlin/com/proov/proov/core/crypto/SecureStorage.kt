package com.proov.proov.core.crypto

/**
 * 토큰 같은 민감한 값을 기기에 안전하게 저장한다.
 * 메모리 캐시와 저장 실패 시의 폴백 정책은 이 계층의 책임이 아니며 호출하는 쪽에서 정한다.
 */
interface SecureStorage {
    /** 저장에 실패하면 실패 결과를 반환한다. 이전에 저장된 값은 그대로 남을 수 있다. */
    suspend fun put(key: SecureKey, value: String): Result<Unit>

    /**
     * 값이 없거나 복호화·조회가 영구적으로 불가능하면 `null`을 반환한다.
     * 영구 실패한 항목은 함께 삭제되므로 호출하는 쪽은 재로그인으로 유도하면 된다.
     * 일시적인 입출력 오류는 삭제하지 않고 예외로 전파한다.
     */
    suspend fun get(key: SecureKey): String?

    suspend fun remove(key: SecureKey): Result<Unit>
}
