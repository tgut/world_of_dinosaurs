package com.example.world_of_dinosaurs_extented.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.world_of_dinosaurs_extented.R

data class BottomNavItem(
    val labelResId: Int,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DinoBottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val items = listOf(
        BottomNavItem(R.string.home, Icons.Default.Home, "home"),
        BottomNavItem(R.string.timeline, Icons.Default.Timeline, "timeline"),
        BottomNavItem(R.string.quiz, Icons.Default.Quiz, "quiz"),
        BottomNavItem(R.string.chat, Icons.AutoMirrored.Filled.Chat, "chat"),
        BottomNavItem(R.string.settings, Icons.Default.Settings, "settings")
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                label = { Text(stringResource(item.labelResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    when (item.route) {
                        "home" -> onNavigateToHome()
                        "timeline" -> onNavigateToTimeline()
                        "quiz" -> onNavigateToQuiz()
                        "chat" -> onNavigateToChat()
                        "settings" -> onNavigateToSettings()
                    }
                }
            )
        }
    }
}
