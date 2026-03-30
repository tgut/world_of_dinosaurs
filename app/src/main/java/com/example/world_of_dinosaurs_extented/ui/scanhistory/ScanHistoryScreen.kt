package com.example.world_of_dinosaurs_extented.ui.scanhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.ui.common.EmptyStateView
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    onDinosaurClick: (String) -> Unit,
    onStartReviewQuiz: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ScanHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.clear_history)) },
            text = { Text(stringResource(R.string.confirm_clear_history)) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearHistory(); showClearDialog = false }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_history)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (uiState.items.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.clear_history))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.canStartReviewQuiz) {
                ExtendedFloatingActionButton(
                    onClick = onStartReviewQuiz,
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    text = { Text(stringResource(R.string.review_quiz)) }
                )
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.items.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_scan_history),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_scan_history_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!uiState.canStartReviewQuiz && uiState.items.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.need_more_scans),
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    items(uiState.items, key = { "${it.dinosaur.id}_${it.scannedAt}" }) { item ->
                        ScanHistoryCard(
                            item = item,
                            language = uiState.language,
                            onClick = { onDinosaurClick(item.dinosaur.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanHistoryCard(
    item: ScanHistoryItem,
    language: String,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val eraColor = eraColor(item.dinosaur.era)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(eraColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.dinosaur.getLocalizedName(language).take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = eraColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.dinosaur.getLocalizedName(language),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.scanned_at, dateFormat.format(Date(item.scannedAt))),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun eraColor(era: DinosaurEra): Color = when (era) {
    DinosaurEra.TRIASSIC -> Color(0xFF8D6E63)
    DinosaurEra.JURASSIC -> Color(0xFF2E7D32)
    DinosaurEra.CRETACEOUS -> Color(0xFF1565C0)
}
