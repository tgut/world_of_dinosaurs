package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsInteractionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val globeTimeout by viewModel.globeRotateTimeout.collectAsStateWithLifecycle(initialValue = 10)
    val ttsSpeed by viewModel.ttsSpeed.collectAsStateWithLifecycle(initialValue = 1.0f)
    val ttsPitch by viewModel.ttsPitch.collectAsStateWithLifecycle(initialValue = 1.0f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_interaction)) },
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
            // Globe auto-rotate
            Text(
                text = stringResource(R.string.globe_rotate_timeout_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.globe_rotate_timeout_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = globeTimeout.toFloat(),
                    onValueChange = { viewModel.setGlobeRotateTimeout(it.toInt()) },
                    valueRange = 3f..30f,
                    steps = 8,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.globe_rotate_timeout_value, globeTimeout),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // TTS
            Text(
                text = stringResource(R.string.voice_settings),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.tts_speed),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = ttsSpeed,
                    onValueChange = { viewModel.setTtsSpeed(it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.tts_speed_value, ttsSpeed),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.tts_pitch),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = ttsPitch,
                    onValueChange = { viewModel.setTtsPitch(it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.tts_pitch_value, ttsPitch),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
