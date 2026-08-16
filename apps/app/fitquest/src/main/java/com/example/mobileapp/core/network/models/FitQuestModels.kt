package com.example.mobileapp.core.network.models

data class RunSyncPayload(
    val total_session_steps: Int,
    val hexes_to_steps: Map<String, Int>
)

data class RunSyncSummary(
    val hexes_defended: Int,
    val hexes_stolen: Int,
    val hexes_newly_captured: Int,
    val xp_earned: Int,
    val new_total_lifetime_steps: Int
)

data class HexDetailResponse(
    val hex_id: String,
    val king_id: String,
    val king_username: String,
    val defense_score_steps: Int,
    val is_owned_by_me: Boolean
)

data class HeatmapResponse(
    val parent_hex_id: String,
    val dominant_king_id: String,
    val dominant_king_username: String,
    val total_hexes_inside: Int
)

data class MapViewportResponse(
    val is_aggregated: Boolean,
    val hexes: List<HexDetailResponse> = emptyList(),
    val heatmaps: List<HeatmapResponse> = emptyList()
)
