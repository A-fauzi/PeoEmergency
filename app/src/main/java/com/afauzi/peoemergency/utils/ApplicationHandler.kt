package com.afauzi.peoemergency.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast

/**
 * Offline data on disk firebase
 */
class ApplicationHandler : Application() {
    override fun onCreate() {
        super.onCreate()

        firebaseOffline()

        isOnline(applicationContext)

    }

    private fun firebaseOffline() {
        val firebaseServiceInstance = FirebaseServiceInstance
        val firebaseDatabase = firebaseServiceInstance.firebaseDatabase

        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        }
        Log.i("Internet", "Not Networking")
        Toast.makeText(applicationContext, "Not Networking, please turn on of internet network", Toast.LENGTH_SHORT).show()
        return false
    }

}