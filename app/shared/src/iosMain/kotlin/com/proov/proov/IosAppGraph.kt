package com.proov.proov

import coil3.PlatformContext
import com.proov.proov.core.localstorage.PREFERENCES_FILE
import com.proov.proov.di.AppGraph
import com.proov.proov.di.IMAGE_CACHE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph {
    @Provides
    fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE

    @Provides
    @Named(IMAGE_CACHE)
    fun provideImageCacheDirectory(): Path {
        val caches = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
            .first() as String
        return "$caches/image_cache".toPath()
    }

    @Provides
    @Named(PREFERENCES_FILE)
    fun providePreferencesFile(): Path {
        val documents = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .first() as String
        return "$documents/datastore/proov.preferences_pb".toPath()
    }
}
