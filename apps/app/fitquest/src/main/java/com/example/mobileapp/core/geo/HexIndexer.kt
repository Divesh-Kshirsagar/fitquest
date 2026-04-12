package com.example.mobileapp.core.geo

import com.example.mobileapp.core.model.GeoPoint

interface HexIndexer {
    fun isAvailable(): Boolean = true

    fun latLngToHexId(latitude: Double, longitude: Double, resolution: Int): String

    fun hexBoundary(hexId: String): List<GeoPoint>

    /**
     * Returns all hex IDs within [ringSize] rings of the hex at the given location.
     * For ringSize=2 at resolution 10 this gives ~19 hexes — a comfortable walking radius.
     *
     * TODO(TESTING): Tune [ringSize] after field testing to balance map visibility vs. performance.
     */
    fun getHexesInRadius(latitude: Double, longitude: Double, resolution: Int, ringSize: Int): List<String>
}
