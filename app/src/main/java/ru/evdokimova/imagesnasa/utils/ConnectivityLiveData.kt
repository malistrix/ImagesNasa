package ru.evdokimova.imagesnasa.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.util.Log
import androidx.lifecycle.LiveData

private const val TAG = "ConnectivityLiveData "

class ConnectivityLiveData(context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    fun post(hasInternet: Boolean) {
        this.postValue(hasInternet)
    }


    override fun onActive() {
        Log.d(TAG, "onActive")
        networkCallback = createNetworkCallback()
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "OnAvail $network ")
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            post(
                networkCapabilities.let {
                    it.hasCapability(NET_CAPABILITY_INTERNET) &&
                            it.hasCapability(NET_CAPABILITY_VALIDATED)
                })
            Log.d(TAG, "CapabilitiesChanged : $networkCapabilities")
        }


        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: $network")
            postValue(false)
        }

    }

}