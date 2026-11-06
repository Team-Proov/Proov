package com.proov.proov.core.crypto

/**
 * 보안 저장소에 보관하는 값의 종류.
 * [id]는 파일 이름, Keychain 계정, GCM의 추가 인증 데이터(AAD)로 쓰이므로 한 번 정하면 바꾸지 않는다.
 * `name`이나 `ordinal`을 대신 쓰면 리팩터링만으로 기존 암호문을 읽지 못하게 된다.
 */
enum class SecureKey(val id: String) {
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
}
