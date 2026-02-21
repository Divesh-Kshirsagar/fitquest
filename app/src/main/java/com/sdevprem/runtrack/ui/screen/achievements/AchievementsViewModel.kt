package com.sdevprem.runtrack.ui.screen.achievements

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Achievement(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val iconResId: Int? = null // Using logic to determine icon in UI for now
)

@HiltViewModel
class AchievementsViewModel @Inject constructor() : ViewModel() {

    private val _achievements = MutableStateFlow(
        listOf(
            Achievement("First Run", "Complete your first run", true),
            Achievement("Marathoner", "Run 42km in total", false),
            Achievement("Early Bird", "Run before 6 AM", true),
            Achievement("Night Owl", "Run after 9 PM", false),
            Achievement("Speedster", "Average pace < 5 min/km", false),
            Achievement("Consistency", "Run 3 days in a row", true),
            Achievement("Mountain Goat", "Gain 500m elevation", false),
            Achievement("Ultra", "Run 50km in one go", false),
            Achievement("Social Butterfly", "Share a run", false),
            Achievement("Techie", "Use a heart rate monitor", true),
            Achievement("Explorer", "Run in 5 new places", false),
            Achievement("Century", "Run 100km total", false)
        )
    )
    val achievements = _achievements.asStateFlow()
}
