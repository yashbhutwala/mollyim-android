package org.signal.cashu

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

@HiltAndroidApp
class CashuApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Realm for the application
        Realm.init(this)
    }
}