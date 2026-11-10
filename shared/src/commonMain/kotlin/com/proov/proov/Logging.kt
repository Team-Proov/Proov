package com.proov.proov

import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter

/** debug에서만 로그를 출력한다. 앱 진입점(Android Application, iOS MainViewController)에서 호출한다. */
fun initLogging(isDebug: Boolean) {
    if (isDebug) Logger.setLogWriters(platformLogWriter()) else Logger.setLogWriters(emptyList())
}
