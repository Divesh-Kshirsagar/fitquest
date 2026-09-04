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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mobileapp.core.network.FitQuestApi
import org.koin.dsl.module

val appModule = module {

    single {
        // ── Networking ────────────────────────────────────────────────────────
        // On a real device (USB debugging), 10.0.2.2 does NOT resolve — that
        // alias only works inside the Android Emulator.
        // Run `adb reverse tcp:8000 tcp:8000` once after plugging in your phone,
        // then 127.0.0.1:8000 on the device tunnels to localhost:8000 on your machine.
        //
        // TODO(production): Add a real OkHttp auth interceptor here that injects
        // the Supabase JWT from your auth store before switching to a real backend.
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FitQuestApi::class.java)
    }


    // ── Dev/Testing Toggles ──────────────────────────────────────────────────
    // TODO(TESTING): Set these to `true` to simulate walk & steps without real sensors.
    //  - useDevLocation = true  → mock GPS walk path (see DevLocationSimulator.DEFAULT_WALK_PATH)
    //  - useDevSteps    = true  → synthetic 2-steps-per-second stream
    //  Set BOTH to `false` for real device testing with actual GPS + step counter.
    val useDevLocation = false
    val useDevSteps = true
    // ─────────────────────────────────────────────────────────────────────────

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

    single { StepSensorManager(get(), useDevSimulator = useDevSteps) }
    single { LocationTrackingManager(get(), useDevSimulator = useDevLocation) }

    single { HexCaptureEngine(get(), get(), get(), get()) }

    // factory (not single) so Voyager can properly scope and dispose the
    // ScreenModel when the screen leaves the backstack. A singleton would keep
    // the Orbit container alive forever and cause stale state on re-entry.
    factory { CaptureScreenModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}



