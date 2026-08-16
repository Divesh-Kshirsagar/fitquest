package com.example.mobileapp

import android.app.Application
import android.util.Log
import com.example.mobileapp.di.appModule
import org.maplibre.android.MapLibre
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FitQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Must catch Throwable (not just Exception) because a missing native .so
        // throws UnsatisfiedLinkError which is a JVM Error — it escapes all
        // Exception handlers and kills the process silently before Logcat fires.
        try {
            MapLibre.getInstance(this)
        } catch (e: Throwable) {
            Log.e("FitQuestApp", "MapLibre native init failed — check jniLibs", e)
        }
        try {
            startKoin {
                androidContext(this@FitQuestApp)
                modules(appModule)
            }
        } catch (e: Exception) {
            Log.e("FitQuestApp", "Koin failed to start", e)
        }
    }
}


