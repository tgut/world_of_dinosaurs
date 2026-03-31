package com.example.world_of_dinosaurs_extented.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onEraClick: (DinosaurEra) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: () -> Unit = {},
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.timeline)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            com.example.world_of_dinosaurs_extented.ui.common.DinoBottomNavBar(
                currentRoute = "timeline",
                onNavigateToHome = onNavigateToHome,
                onNavigateToTimeline = {},
                onNavigateToQuiz = onNavigateToQuiz,
                onNavigateToChat = onNavigateToChat,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val eras = DinosaurEra.entries
                eras.forEachIndexed { index, era ->
                    item(key = era.name) {
                        EraTimelineItem(
                            era = era,
                            dinosaurs = uiState.eraGroups[era] ?: emptyList(),
                            language = uiState.language,
                            isFirst = index == 0,
                            isLast = index == eras.lastIndex,
                            onClick = { onEraClick(era) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EraTimelineItem(
    era: DinosaurEra,
    dinosaurs: List<Dinosaur>,
    language: String,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val eraColor = eraColor(era)

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline line + dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(eraColor)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        // Era card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = 16.dp)
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = eraColor.copy(alpha = 0.08f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = eraLabel(era),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = eraColor
                        )
                        Text(
                            text = "${era.startMya} – ${era.endMya} Ma",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = eraColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dinosaur count
                Text(
                    text = stringResource(R.string.timeline_explore, dinosaurs.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (dinosaurs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Mini dinosaur avatars
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dinosaurs.take(8), key = { it.id }) { dino ->
                            DinoMiniAvatar(
                                name = dino.getLocalizedName(language),
                                eraColor = eraColor
                            )
                        }
                        if (dinosaurs.size > 8) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .background(eraColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${dinosaurs.size - 8}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = eraColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DinoMiniAvatar(name: String, eraColor: Color) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(eraColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = eraColor
        )
    }
}

@Composable
private fun eraLabel(era: DinosaurEra): String = when (era) {
    DinosaurEra.TRIASSIC -> stringResource(R.string.triassic)
    DinosaurEra.JURASSIC -> stringResource(R.string.jurassic)
    DinosaurEra.CRETACEOUS -> stringResource(R.string.cretaceous)
}

private fun eraColor(era: DinosaurEra): Color = when (era) {
    DinosaurEra.TRIASSIC -> Color(0xFF8D6E63)
    DinosaurEra.JURASSIC -> Color(0xFF2E7D32)
    DinosaurEra.CRETACEOUS -> Color(0xFF1565C0)
}
