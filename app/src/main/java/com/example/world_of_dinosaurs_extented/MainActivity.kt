package com.example.world_of_dinosaurs_extented

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.navigation.DinoNavGraph
import com.example.world_of_dinosaurs_extented.ui.common.PrivacyConsentDialog
import com.example.world_of_dinosaurs_extented.ui.theme.DinoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val themeMode by settingsManager.themeFlow.collectAsState(initial = "system")
            val privacyConsentAccepted by settingsManager.privacyConsentAcceptedFlow.collectAsState(initial = false)
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            DinoTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    DinoNavGraph(navController = navController)

                    if (!privacyConsentAccepted) {
                        PrivacyConsentDialog(
                            onAccept = {
                                scope.launch { settingsManager.acceptPrivacyConsent() }
                            },
                            onDecline = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
