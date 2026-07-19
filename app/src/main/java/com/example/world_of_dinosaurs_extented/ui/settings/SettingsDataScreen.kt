package com.example.world_of_dinosaurs_extented.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDataScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val exportState by viewModel.exportState.collectAsStateWithLifecycle(initialValue = ExportUiState())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Management") },
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
            Text(
                text = "Export Favorites",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Export your favorite dinosaurs to a JSON file",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.exportFavorites() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !exportState.isExporting
            ) {
                if (exportState.isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (exportState.isExporting) "Exporting..." else "Export Favorites JSON")
            }

            if (exportState.exportSuccessPath != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Exported to: ${exportState.exportSuccessPath}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                exportState.exportShareIntent?.let { intent ->
                                    context.startActivity(Intent.createChooser(intent, "Share Export"))
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Export")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { viewModel.dismissExportResult() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            if (exportState.exportError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = exportState.exportError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { viewModel.dismissExportResult() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}
