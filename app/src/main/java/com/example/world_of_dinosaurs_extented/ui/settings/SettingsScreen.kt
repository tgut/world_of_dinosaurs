package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle(initialValue = "en")
    val theme by viewModel.theme.collectAsStateWithLifecycle(initialValue = "system")

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
        ) {
            // Language section
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = language == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text(stringResource(R.string.english)) }
                )
                FilterChip(
                    selected = language == "zh",
                    onClick = { viewModel.setLanguage("zh") },
                    label = { Text(stringResource(R.string.chinese)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme section
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = theme == "system",
                    onClick = { viewModel.setTheme("system") },
                    label = { Text(stringResource(R.string.system_default)) }
                )
                FilterChip(
                    selected = theme == "light",
                    onClick = { viewModel.setTheme("light") },
                    label = { Text(stringResource(R.string.light)) }
                )
                FilterChip(
                    selected = theme == "dark",
                    onClick = { viewModel.setTheme("dark") },
                    label = { Text(stringResource(R.string.dark)) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // About section
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
