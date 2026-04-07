package com.example.world_of_dinosaurs_extented.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import android.view.View
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
    onAskAI: (String) -> Unit = {},
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Banner 广告状态
    var bannerAd by remember { mutableStateOf<View?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.adManager.loadBanner(
            unitId = viewModel.adUnitIds.bannerDetail,
            onLoaded = { bannerAd = it },
            onFailed = {}
        )
    }

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
        },
        bottomBar = {
            // 穿山甲 Banner 广告 — 320×50，固定底部
            bannerAd?.let { ad ->
                BannerAdView(
                    ad = ad,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.error != null -> ErrorView(message = uiState.error, modifier = Modifier.padding(padding))
            uiState.dinosaur != null -> {
                val dino = uiState.dinosaur!!
                val language = uiState.language
                val altLanguage = if (language == "en") "zh" else "en"
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
                        if (uiState.showTranslation) {
                            val altName = dino.getLocalizedName(altLanguage)
                            if (altName.isNotBlank()) {
                                Text(
                                    text = altName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
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

                        // Action buttons: 3D/AR/AI + Read Aloud + Translate
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (Model3dConfig.hasModel(dino.id)) {
                                OutlinedButton(onClick = { onView3D(dino.id) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.view_in_3d))
                                }
                                // AR feature temporarily hidden
                                // OutlinedButton(onClick = { onViewAR(dino.id) }) { ... }
                            }
                            OutlinedButton(onClick = { onAskAI(dino.id) }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.ask_ai))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Read Aloud / Stop button
                            if (uiState.isSpeaking) {
                                OutlinedButton(onClick = { viewModel.stopReading() }) {
                                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.stop_reading))
                                }
                            } else {
                                OutlinedButton(onClick = { viewModel.readAloud() }) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.read_aloud))
                                }
                            }
                            // Translate toggle button
                            OutlinedButton(
                                onClick = { viewModel.toggleTranslation() },
                                colors = if (uiState.showTranslation) ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.translate))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

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
                            if (uiState.showTranslation) {
                                val altDesc = dino.getLocalizedDescription(altLanguage)
                                if (altDesc.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = altDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
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
                            val altFacts = if (uiState.showTranslation) dino.getLocalizedFacts(altLanguage) else emptyList()
                            facts.forEachIndexed { index, fact ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("• ", style = MaterialTheme.typography.bodyMedium)
                                    Column {
                                        Text(text = fact, style = MaterialTheme.typography.bodyMedium)
                                        if (uiState.showTranslation && index < altFacts.size) {
                                            Text(
                                                text = altFacts[index],
                                                style = MaterialTheme.typography.bodySmall,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }
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
                            if (uiState.showTranslation) {
                                val altHabitat = dino.getLocalizedHabitat(altLanguage)
                                if (altHabitat.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = altHabitat,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerAdView(
    ad: View,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ad },
        modifier = modifier
    )
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
