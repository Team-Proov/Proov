package com.proov.proov.core.network.di

import com.proov.proov.core.network.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_MILLIS = 15_000L

private object KermitKtorLogger : Logger {
    private val kermit = co.touchlab.kermit.Logger.withTag("Ktor")

    override fun log(message: String) = kermit.d { message }
}

fun createHttpClient(
    baseUrl: String = BuildKonfig.BASE_URL
): HttpClient = HttpClient {
    // 데이터 직렬화 , 역질렬화
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            },
        )
    }
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MILLIS
        connectTimeoutMillis = TIMEOUT_MILLIS
    }
    // INFO는 요청·응답 라인만 남기므로 Authorization 헤더가 로그에 노출되지 않는다
    // 출력 여부는 Kermit 전역 설정이 정한다(release에서는 LogWriter가 비어 있어 아무것도 출력되지 않는다)
    install(Logging) {
        level = LogLevel.INFO
        logger = KermitKtorLogger
    }
    defaultRequest {
        url(baseUrl)
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}
