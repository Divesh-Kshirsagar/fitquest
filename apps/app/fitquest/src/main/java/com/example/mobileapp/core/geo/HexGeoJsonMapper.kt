package com.example.mobileapp.core.geo

import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon

object HexGeoJsonMapper {
    fun toFeatureCollection(hexIndexer: HexIndexer, hexIds: Collection<String>): FeatureCollection {
        val features = hexIds.mapNotNull { hexId ->
            runCatching {
                val boundary = hexIndexer.hexBoundary(hexId)
                if (boundary.isEmpty()) return@runCatching null

                val points = boundary.map { Point.fromLngLat(it.longitude, it.latitude) }
                val closedPoints = points + points.first()
                val polygon = Polygon.fromLngLats(listOf(closedPoints))

                Feature.fromGeometry(polygon).apply {
                    addStringProperty("hexId", hexId)
                }
            }.getOrNull()
        }
        return FeatureCollection.fromFeatures(features)
    }

    /**
     * Convenience wrapper: converts hex IDs straight to a GeoJSON JSON string.
     * Intended to be called on a background thread to avoid JNI work on the UI thread.
     */
    fun toGeoJsonString(hexIndexer: HexIndexer, hexIds: Collection<String>): String =
        toFeatureCollection(hexIndexer, hexIds).toJson()
}