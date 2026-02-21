package com.sdevprem.runtrack.domain.model

import com.google.android.gms.maps.model.LatLng

data class ColoredPolygon(
    val id: Long, // H3 Index
    val points: List<LatLng>,
    val color: Long // ARGB Color
)
