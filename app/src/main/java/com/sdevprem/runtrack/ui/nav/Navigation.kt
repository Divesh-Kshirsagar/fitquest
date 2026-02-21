package com.sdevprem.runtrack.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sdevprem.runtrack.ui.nav.Destination.CurrentRun
import com.sdevprem.runtrack.ui.screen.common.ComingSoonScreen
import com.sdevprem.runtrack.ui.screen.currentrun.CurrentRunScreen
import com.sdevprem.runtrack.ui.screen.onboard.OnBoardScreen
import com.sdevprem.runtrack.ui.screen.profile.EditProfileScreen
import com.sdevprem.runtrack.ui.screen.achievements.AchievementsScreen
import com.sdevprem.runtrack.ui.screen.settings.SettingsScreen
import com.sdevprem.runtrack.ui.screen.profile.ContactScreen
import com.sdevprem.runtrack.ui.screen.profile.ProfileScreen
import com.sdevprem.runtrack.ui.screen.runstats.RunStatsScreen

@Composable
fun Navigation(
    navController: NavHostController,
) {
    SetupNavGraph(
        navController = navController,
    )
}

@Composable
private fun SetupNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Home.route
    ) {
        homeNavigation(navController)

        composable(
            route = BottomNavDestination.Profile.route
        ) {
            ProfileScreen(navController = navController)
        }

        composable(
            route = CurrentRun.route,
            deepLinks = CurrentRun.deepLinks
        ) {
            CurrentRunScreen(navController)
        }

        composable(
            route = Destination.OnBoardingDestination.route
        ) {
            OnBoardScreen(navController = navController)
        }

        composable(route = Destination.RunStats.route) {
            RunStatsScreen(
                navigateUp = { navController.navigateUp() }
            )
        }

        composable(route = Destination.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }

        composable(route = Destination.Achievements.route) {
            AchievementsScreen(navController = navController)
        }

        composable(route = Destination.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(route = Destination.Contact.route) {
            ContactScreen(navController = navController)
        }

        composable(route = Destination.ComingSoon.route) {
            ComingSoonScreen(navController = navController)
        }
    }

}