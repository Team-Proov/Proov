package com.proov.proov

import android.app.Application
import android.content.pm.ApplicationInfo
import dev.zacsweers.metro.createGraphFactory

class ProovApplication : Application() {
    // 앱 시작과 함께 만들어 MainActivity 등 외부 진입점이 꺼내 쓴다
    lateinit var appGraph: AndroidAppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        // BuildConfig 없이 debuggable 플래그로 debug 빌드를 판별한다
        initLogging(isDebug = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        appGraph = createGraphFactory<AndroidAppGraph.Factory>().create(this)
    }
}
