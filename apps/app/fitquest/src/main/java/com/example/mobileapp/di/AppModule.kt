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

    // ── Dev/Testing Toggles ──────────────────────────────────────────────────
    // TODO(TESTING): Set these to `true` to simulate walk & steps without real sensors.
    //  - useDevLocation = true  → mock GPS walk path (see DevLocationSimulator.DEFAULT_WALK_PATH)
    //  - useDevSteps    = true  → synthetic 2-steps-per-second stream
    //  Set BOTH to `false` for real device testing with actual GPS + step counter.
    val useDevLocation = false
    val useDevSteps = false
    // ─────────────────────────────────────────────────────────────────────────

    single {
        Room.databaseBuilder(
            get(),
            FitQuestDatabase::class.java,
            "fitquest.db"
        ).build()
    }
    single { get<FitQuestDatabase>().hexDao() }
    single<HexRepository> { RoomHexRepository(get()) }

    single<HexIndexer> { UberH3HexIndexer() }

    single { StepSensorManager(get(), useDevSimulator = useDevSteps) }
    single { LocationTrackingManager(get(), useDevSimulator = useDevLocation) }

    single { HexCaptureEngine(get(), get(), get(), get()) }

    // CaptureScreenModel now takes HexIndexer for off-thread GeoJSON computation.
    // TODO: If multiple screens need independent sessions, switch this to a scoped factory.
    single { CaptureScreenModel(get(), get(), get()) }
}
