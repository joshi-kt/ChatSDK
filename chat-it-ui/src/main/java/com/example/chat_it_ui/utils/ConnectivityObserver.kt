package com.example.chat_it_ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

internal object ConnectivityObserver {

    private var connectivityManager : ConnectivityManager? = null

    fun observeConnectivity(context: Context) = callbackFlow {

        connectivityManager = context.getSystemService<ConnectivityManager>()

        val callback = object : NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }

        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()

        val isActiveNetwork = connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false

        trySend(isActiveNetwork)

        connectivityManager?.registerNetworkCallback(networkRequest, callback)

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

}