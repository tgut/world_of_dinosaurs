package com.example.world_of_dinosaurs_extented.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.data.model3d.Model3dConfig
import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.ui.common.ErrorView
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator
import com.example.world_of_dinosaurs_extented.ui.theme.CretaceousColor
import com.example.world_of_dinosaurs_extented.ui.theme.JurassicColor
import com.example.world_of_dinosaurs_extented.ui.theme.TriassicColor
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    onView3D: (String) -> Unit,
    onViewAR: (String) -> Unit,
    onViewOnMap: (String) -> Unit = {},
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.dinosaur?.getLocalizedName(uiState.language) ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    uiState.dinosaur?.let { dino ->
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                if (dino.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(R.string.favorites),
                                tint = if (dino.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.error != null -> ErrorView(message = uiState.error, modifier = Modifier.padding(padding))
            uiState.dinosaur != null -> {
                val dino = uiState.dinosaur!!
                val language = uiState.language
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero image
                    AsyncImage(
                        model = dino.imageUrl,
                        contentDescription = dino.getLocalizedName(language),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Name and scientific name
                        Text(
                            text = dino.getLocalizedName(language),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = dino.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Basic info banner for non-featured dinosaurs
                        if (!dino.isFeatured) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.basic_info),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Era badge
                        val eraColor = when (dino.era) {
                            DinosaurEra.TRIASSIC -> TriassicColor
                            DinosaurEra.JURASSIC -> JurassicColor
                            DinosaurEra.CRETACEOUS -> CretaceousColor
                        }
                        val eraName = when (dino.era) {
                            DinosaurEra.TRIASSIC -> stringResource(R.string.triassic)
                            DinosaurEra.JURASSIC -> stringResource(R.string.jurassic)
                            DinosaurEra.CRETACEOUS -> stringResource(R.string.cretaceous)
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text("$eraName · ${dino.periodYearsAgo}") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = eraColor.copy(alpha = 0.2f))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3D/AR buttons
                        if (Model3dConfig.hasModel(dino.id)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { onView3D(dino.id) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.view_in_3d))
                                }
                                OutlinedButton(onClick = { onViewAR(dino.id) }) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.view_in_ar))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Stats card — only show if there's actual data
                        val hasStats = dino.lengthMeters != null || dino.heightMeters != null || dino.weightKg != null
                        if (hasStats) {
                            StatsCard(dino = dino)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Description — only show if non-empty
                        val desc = dino.getLocalizedDescription(language)
                        if (desc.isNotBlank()) {
                            Text(
                                text = stringResource(R.string.description),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Fun facts — only show if non-empty
                        val facts = dino.getLocalizedFacts(language)
                        if (facts.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.fun_facts),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            facts.forEach { fact ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("• ", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = fact, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Discovery info
                        if (dino.discoveryYear != null) {
                            Text(
                                text = stringResource(R.string.discovery_info),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.discovered_in, dino.discoveryYear),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (dino.discoveryLocation.isNotBlank()) {
                                Text(
                                    text = stringResource(R.string.discovered_at, dino.discoveryLocation),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { onViewOnMap(dino.id) }) {
                                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.view_on_map))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Habitat — only show if non-empty
                        val habitat = dino.getLocalizedHabitat(language)
                        if (habitat.isNotBlank()) {
                            Text(
                                text = stringResource(R.string.habitat),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = habitat,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(dino: Dinosaur) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dino.lengthMeters?.let {
                StatItem(label = stringResource(R.string.length), value = "${it}${stringResource(R.string.meters)}")
            }
            dino.heightMeters?.let {
                StatItem(label = stringResource(R.string.height), value = "${it}${stringResource(R.string.meters)}")
            }
            dino.weightKg?.let {
                val display = if (it >= 1000) "${it / 1000}t" else "${it}${stringResource(R.string.kg)}"
                StatItem(label = stringResource(R.string.weight), value = display)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
