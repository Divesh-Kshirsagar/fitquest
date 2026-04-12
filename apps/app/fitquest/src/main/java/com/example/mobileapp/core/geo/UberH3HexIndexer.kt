package com.example.mobileapp.core.geo

import android.util.Log
import com.example.mobileapp.core.model.GeoPoint
import com.uber.h3core.H3Core

class UberH3HexIndexer : HexIndexer {
    private val h3CoreOrNull: H3Core? by lazy {
        runCatching {
            // Load native H3 libraries from app-packaged jniLibs.
            H3Core.newSystemInstance()
        }
            .onFailure { throwable ->
                Log.e("UberH3HexIndexer", "Failed to initialize H3Core from jniLibs", throwable)
            }
            .getOrNull()
    }

    override fun isAvailable(): Boolean = h3CoreOrNull != null

    override fun latLngToHexId(latitude: Double, longitude: Double, resolution: Int): String {
        val h3Core = h3CoreOrNull ?: return ""
        return h3Core.latLngToCellAddress(latitude, longitude, resolution)
    }

    override fun hexBoundary(hexId: String): List<GeoPoint> {
        val h3Core = h3CoreOrNull ?: return emptyList()
        if (hexId.isBlank()) return emptyList()
        return h3Core.cellToBoundary(hexId).map { GeoPoint(it.lat, it.lng) }
    }

    override fun getHexesInRadius(
        latitude: Double,
        longitude: Double,
        resolution: Int,
        ringSize: Int
    ): List<String> {
        val h3Core = h3CoreOrNull ?: return emptyList()
        val centerHex = h3Core.latLngToCellAddress(latitude, longitude, resolution)
        return h3Core.gridDisk(centerHex, ringSize)
    }
}
