package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.world_of_dinosaurs_extented.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNavigateToAppearance: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToService: () -> Unit,
    onNavigateToInteraction: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategoryItem(
                icon = Icons.Default.AccountCircle,
                title = "User Profile",
                subtitle = "Manage your personal information and account",
                onClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.Language,
                title = "Appearance",
                subtitle = "Language and theme settings",
                onClick = onNavigateToAppearance
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.SmartToy,
                title = "AI Chat",
                subtitle = "Configure AI provider and API keys",
                onClick = onNavigateToChat
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.Map,
                title = "Services",
                subtitle = "Map and image recognition service settings",
                onClick = onNavigateToService
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.MusicNote,
                title = "Voice & Interaction",
                subtitle = "TTS voice, globe auto-rotation, and more",
                onClick = onNavigateToInteraction
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.FileDownload,
                title = "Data Management",
                subtitle = "Export favorites to JSON file",
                onClick = onNavigateToData
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsCategoryItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about),
                subtitle = "App info, support the developer, and submit feedback",
                onClick = onNavigateToAbout
            )
        }
    }
}

@Composable
private fun SettingsCategoryItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
