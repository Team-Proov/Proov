package com.proov.proov.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path

const val IMAGE_CACHE = "imageCache"

private const val MEMORY_CACHE_PERCENT = 0.15
private const val DISK_CACHE_BYTES = 50L * 1024 * 1024

/** 플랫폼 그래프가 [PlatformContext]와 `@Named(IMAGE_CACHE)` 경로를 제공한다. */
@ContributesTo(AppScope::class)
interface ImageLoaderProvider {
    @Provides
    @SingleIn(AppScope::class)
    fun provideImageLoader(
        context: PlatformContext,
        @Named(IMAGE_CACHE) cacheDirectory: Path,
    ): ImageLoader = ImageLoader.Builder(context)
        .memoryCache { MemoryCache.Builder().maxSizePercent(context, MEMORY_CACHE_PERCENT).build() }
        .diskCache { DiskCache.Builder().directory(cacheDirectory).maxSizeBytes(DISK_CACHE_BYTES).build() }
        .build()
}
