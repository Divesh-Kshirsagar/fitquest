package com.sdevprem.runtrack.ui.nav

import androidx.navigation.NavController
import androidx.navigation.navDeepLink

sealed class Destination(val route: String) {

    object OnBoardingDestination : Destination("on_boarding") {
        fun navigateToHome(navController: NavController) {
            navController.navigate(BottomNavDestination.Home.route) {
                popUpTo(route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    object CurrentRun : Destination("current_run") {
        val currentRunUriPattern = "https://runtrack.sdevprem.com/$route"
        val deepLinks = listOf(
            navDeepLink {
                uriPattern = currentRunUriPattern
            }
        )
    }

    data object RunStats : Destination("run_stats")
    data object EditProfile : Destination("edit_profile")
    data object Achievements : Destination("achievements")
    data object Settings : Destination("settings")
    data object Contact : Destination("contact")
    data object ComingSoon : Destination("coming_soon")

    //global navigation
    companion object {
        fun navigateToCurrentRunScreen(navController: NavController) {
            navController.navigate(CurrentRun.route)
        }

        fun navigateToAchievementsScreen(navController: NavController) {
            navController.navigate(Achievements.route)
        }

        fun navigateToSettingsScreen(navController: NavController) {
            navController.navigate(Settings.route)
        }

        fun navigateToContactScreen(navController: NavController) {
            navController.navigate(Contact.route)
        }
    }

}
