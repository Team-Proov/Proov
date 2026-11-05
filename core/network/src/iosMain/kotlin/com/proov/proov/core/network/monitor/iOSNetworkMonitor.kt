package com.proov.proov.core.network.monitor

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_queue_create

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class iOSNetworkMonitor : NetworkMonitor {
    @OptIn(ExperimentalForeignApi::class)
    override val isOnline: Flow<Boolean> = callbackFlow {
        val monitor = nw_path_monitor_create()
        // 콜백을 받을 전용 큐: 지정하지 않으면 핸들러가 호출되지 않는다
        val queue = dispatch_queue_create("com.proov.proov.network.monitor", null)
        nw_path_monitor_set_queue(monitor, queue)

        // start 직후 현재 경로가 한 번 전달되므로 Android처럼 초기값을 따로 방출하지 않는다
        nw_path_monitor_set_update_handler(monitor) { path ->
            trySend(nw_path_get_status(path) == nw_path_status_satisfied)
        }
        nw_path_monitor_start(monitor)

        awaitClose { nw_path_monitor_cancel(monitor) }
    }
        .distinctUntilChanged()
        .conflate()
}
