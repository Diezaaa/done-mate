package com.example.donemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.donemate.MainActivityUiState.Loading
import com.example.donemate.ui.theme.DoneMateTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: Maybe learn more about splash screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var mainActivityUiState by mutableStateOf<MainActivityUiState>(
            Loading
        )

        // TODO: Learn more about lifecycles. Try to understand this code again
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.uiState.onEach {
                    mainActivityUiState = it
                }
                    .map {
                        mainActivityUiState.let {
                            if (it is MainActivityUiState.Success) it.isDarkTheme else false
                        }
                    }
                    .distinctUntilChanged()
                    .collect { isDarkTheme ->
                        enableEdgeToEdge(
                            // TODO: MAYBE I should store whether or not the dark theme is on in the resources
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { isDarkTheme }
                        )
                    }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            mainActivityViewModel.uiState.value.shouldKeepSplashScreen()
        }

        setContent {
            mainActivityUiState.let {
                if (it is MainActivityUiState.Success) {
                    DoneMateTheme(darkTheme = it.isDarkTheme) {
                        DoneMateApp(
                            userProfile = it.userProfile,
                            isUserVerified = it.isUserVerified,
                            onOfflineDialogDismiss = mainActivityViewModel::dismissOfflineDialog,
                            wasOfflineDialogShown = it.wasOfflineDialogShown
                        )
                    }
                }
            }
        }
    }
}