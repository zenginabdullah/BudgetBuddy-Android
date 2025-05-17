package com.budgetbuddy.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Ağa bağlanma durumu türleri
sealed class ConnectivityStatus {
    object Available : ConnectivityStatus()
    object Unavailable : ConnectivityStatus()
    object Lost : ConnectivityStatus()
}

// İnterface: Bağlantı durumunu gözlemlemek için
interface ConnectivityObserver {
    fun observe(): Flow<ConnectivityStatus>
}

// Gerçek gözlemci sınıfı: Android'in ConnectivityManager'ını kullanır
class NetworkConnectivityObserver(
    private val context: Context
) : ConnectivityObserver {

    override fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityStatus.Available)
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Lost)
            }

            override fun onUnavailable() {
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
