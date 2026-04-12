package com.example.mobileapp

import android.app.Application
import com.example.mobileapp.di.appModule
import org.maplibre.android.MapLibre
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FitQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        startKoin {
            androidContext(this@FitQuestApp)
            modules(appModule)
        }
    }
}


