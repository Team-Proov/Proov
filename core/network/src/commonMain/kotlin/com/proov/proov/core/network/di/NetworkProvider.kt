package com.proov.proov.core.network.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient

/** shared의 AppGraph가 컴파일 시점에 자동으로 수집한다. */
@ContributesTo(AppScope::class)
interface NetworkProvider {
    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpClient(): HttpClient = createHttpClient()
}
