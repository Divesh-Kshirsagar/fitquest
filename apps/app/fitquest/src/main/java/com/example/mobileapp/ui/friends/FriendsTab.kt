package com.example.mobileapp.ui.friends

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.mobileapp.ui.achievements.AchievementsTab

object FriendsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Star)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Trophies",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        // Friends feature is out of scope for this offline sprint.
        // Delegating to the complete Achievements & Trophies tab.
        AchievementsTab.Content()
    }
}
