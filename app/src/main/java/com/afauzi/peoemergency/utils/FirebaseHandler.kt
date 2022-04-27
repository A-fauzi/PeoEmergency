package com.afauzi.peoemergency.utils

import android.app.Application

/**
 * Offline data on disk firebase
 */
class FirebaseHandler : Application() {
    override fun onCreate() {
        super.onCreate()

        val firebaseServiceInstance = FirebaseServiceInstance
        val firebaseDatabase = firebaseServiceInstance.firebaseDatabase

        firebaseDatabase.setPersistenceEnabled(true)
    }
}