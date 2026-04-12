package com.example.mobileapp.core.geo

import com.example.mobileapp.core.model.GeoPoint
import org.junit.Assert.assertTrue
import org.junit.Test

class HexGeoJsonMapperTest {
    @Test
    fun `toFeatureCollectionJson returns polygon feature json for one valid hex`() {
        val indexer = object : HexIndexer {
            override fun latLngToHexId(latitude: Double, longitude: Double, resolution: Int): String {
                return "test_hex"
            }

            override fun hexBoundary(hexId: String): List<GeoPoint> {
                return listOf(
                    GeoPoint(12.9716, 77.5946),
                    GeoPoint(12.9720, 77.5950),
                    GeoPoint(12.9712, 77.5953),
                    GeoPoint(12.9708, 77.5948),
                    GeoPoint(12.9710, 77.5942),
                    GeoPoint(12.9714, 77.5941)
                )
            }
        }
        val hexId = "test_hex"

        val collectionJson = HexGeoJsonMapper.toFeatureCollectionJson(indexer, listOf(hexId))

        assertTrue(collectionJson.contains("\"type\":\"FeatureCollection\""))
        assertTrue(collectionJson.contains("\"type\":\"Polygon\""))
        assertTrue(collectionJson.contains(hexId))
    }
}







