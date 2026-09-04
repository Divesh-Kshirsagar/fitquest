package com.example.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.mobileapp.core.data.local.UserProfileRepository
import com.example.mobileapp.ui.auth.OnboardingScreen
import com.example.mobileapp.ui.main.MainHubScreen
import com.example.mobileapp.ui.theme.MobileAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val userProfileRepository: UserProfileRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileAppTheme {
                var initialScreenReady by remember { mutableStateOf(false) }
                var isOnboardingDone by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val profile = userProfileRepository.getProfile()
                    isOnboardingDone = profile.isOnboardingCompleted
                    initialScreenReady = true
                }

                if (initialScreenReady) {
                    val startScreen = if (isOnboardingDone) MainHubScreen() else OnboardingScreen()
                    Navigator(startScreen) { navigator ->
                        SlideTransition(navigator)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
