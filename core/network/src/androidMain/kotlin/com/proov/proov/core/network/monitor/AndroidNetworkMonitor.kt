package com.proov.proov.core.network.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Collections

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class AndroidNetworkMonitor(private val context: Context) : NetworkMonitor {
    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        if (connectivityManager == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val initialIsOnline = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false
        trySend(initialIsOnline)

        val callback = object : ConnectivityManager.NetworkCallback() {
            private val validNetworks = Collections.synchronizedSet(mutableSetOf<Network>())

            override fun onAvailable(network: Network) {
                validNetworks += network
                trySend(validNetworks.isNotEmpty())
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isValidated = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED,
                )
                if (isValidated) {
                    validNetworks += network
                } else {
                    validNetworks -= network
                }
                trySend(validNetworks.isNotEmpty())
            }

            override fun onLost(network: Network) {
                validNetworks -= network
                trySend(validNetworks.isNotEmpty())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (e: SecurityException) {
            // 예외를 전파하면 수집하는 쪽이 종료될 수 있으므로 오프라인으로 처리한다
            Logger.e("NetworkMonitor registerNetworkCallback 보안 예외 발생: ${e.message}")
            trySend(false)
        }

        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: IllegalArgumentException) {
                Logger.e("NetworkMonitor unregisterNetworkCallback 에러: ${e.message}")
            }
        }
    }
        .distinctUntilChanged()
        .conflate()
}
