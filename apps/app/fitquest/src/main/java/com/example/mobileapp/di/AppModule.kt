package com.example.mobileapp.di

import androidx.room.Room
import com.example.mobileapp.core.capture.HexCaptureEngine
import com.example.mobileapp.core.data.local.FitQuestDatabase
import com.example.mobileapp.core.data.local.HexRepository
import com.example.mobileapp.core.data.local.RoomHexRepository
import com.example.mobileapp.core.geo.HexIndexer
import com.example.mobileapp.core.geo.UberH3HexIndexer
import com.example.mobileapp.core.sensors.LocationTrackingManager
import com.example.mobileapp.core.sensors.StepSensorManager
import com.example.mobileapp.features.capture.CaptureScreenModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            FitQuestDatabase::class.java,
            "fitquest.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<FitQuestDatabase>().hexDao() }
    single { get<FitQuestDatabase>().userDao() }
    single { get<FitQuestDatabase>().runSessionDao() }
    single { get<FitQuestDatabase>().dailyQuestDao() }
    single { get<FitQuestDatabase>().achievementDao() }

    single<HexRepository> { RoomHexRepository(get()) }
    single<com.example.mobileapp.core.data.local.UserProfileRepository> { 
        com.example.mobileapp.core.data.local.RoomUserProfileRepository(get()) 
    }
    single<com.example.mobileapp.core.data.local.RunSessionRepository> { 
        com.example.mobileapp.core.data.local.RoomRunSessionRepository(get()) 
    }
    single<com.example.mobileapp.core.data.local.QuestRepository> { 
        com.example.mobileapp.core.data.local.RoomQuestRepository(get(), get()) 
    }
    single<com.example.mobileapp.core.data.local.AchievementRepository> { 
        com.example.mobileapp.core.data.local.RoomAchievementRepository(get(), get()) 
    }

    single<HexIndexer> { UberH3HexIndexer() }

    single { StepSensorManager(get()) }
    single { LocationTrackingManager(get()) }

    single { HexCaptureEngine(get(), get(), get(), get()) }

    // factory (not single) so Voyager can properly scope and dispose the
    // ScreenModel when the screen leaves the backstack. A singleton would keep
    // the Orbit container alive forever and cause stale state on re-entry.
    factory { CaptureScreenModel(get(), get(), get(), get(), get(), get(), get()) }
}



