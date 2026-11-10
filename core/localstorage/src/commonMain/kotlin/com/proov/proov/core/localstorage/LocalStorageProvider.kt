package com.proov.proov.core.localstorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path

/** 플랫폼 그래프가 제공하는 Preferences 파일 경로의 한정자이다. 확장자는 `.preferences_pb`여야 한다. */
const val PREFERENCES_FILE = "proov_preferencesFile"

/**
 * 같은 파일에 DataStore 인스턴스를 둘 이상 만들면 오류가 나므로 앱 전체에서 하나만 둔다.
 * 읽기·쓰기는 플랫폼과 무관하게 공통이고, 파일 경로만 플랫폼 그래프가 제공한다.
 */
@ContributesTo(AppScope::class)
interface LocalStorageProvider {
    @Provides
    @SingleIn(AppScope::class)
    fun providePreferences(@Named(PREFERENCES_FILE) file: Path): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(produceFile = { file })
}
