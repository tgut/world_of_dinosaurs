package com.example.world_of_dinosaurs_extented.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.ui.common.*
import com.example.world_of_dinosaurs_extented.ui.home.components.FilterChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDinosaurClick: (String) -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecognition: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onClose = {
                        showSearch = false
                        viewModel.onSearchQueryChanged("")
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onNavigateToRecognition) {
                            Icon(Icons.Default.CameraAlt, contentDescription = stringResource(R.string.identify_dinosaur))
                        }
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_dinosaurs))
                        }
                        IconButton(onClick = viewModel::toggleViewMode) {
                            Icon(
                                if (uiState.isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Toggle view"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            DinoBottomNavBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToTimeline = onNavigateToTimeline,
                onNavigateToQuiz = onNavigateToQuiz,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterChips(
                selectedEra = uiState.selectedEra,
                selectedDiet = uiState.selectedDiet,
                onEraSelected = viewModel::onEraFilterChanged,
                onDietSelected = viewModel::onDietFilterChanged
            )

            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorView(message = uiState.error, onRetry = viewModel::retry)
                uiState.dinosaurs.isEmpty() -> EmptyStateView(title = stringResource(R.string.no_results))
                uiState.isGridView -> DinosaurGrid(uiState.dinosaurs, uiState.language, onDinosaurClick)
                else -> DinosaurList(uiState.dinosaurs, uiState.language, onDinosaurClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.search_dinosaurs)) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.back))
            }
        }
    )
}

@Composable
private fun DinosaurGrid(
    dinosaurs: List<Dinosaur>,
    language: String,
    onClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dinosaurs, key = { it.id }) { dino ->
            DinosaurGridCard(dino = dino, language = language, onClick = { onClick(dino.id) })
        }
    }
}

@Composable
private fun DinosaurList(
    dinosaurs: List<Dinosaur>,
    language: String,
    onClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dinosaurs, key = { it.id }) { dino ->
            DinosaurListCard(dino = dino, language = language, onClick = { onClick(dino.id) })
        }
    }
}

@Composable
private fun DinosaurGridCard(dino: Dinosaur, language: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                DinoImageOrPlaceholder(
                    imageUrl = dino.imageUrl,
                    name = dino.getLocalizedName(language),
                    era = dino.era,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                if (dino.isFeatured) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = stringResource(R.string.featured),
                        tint = Color(0xFFFFC107),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = dino.getLocalizedName(language),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(eraLabel(dino.era), style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DinosaurListCard(dino: Dinosaur, language: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            DinoImageOrPlaceholder(
                imageUrl = dino.imageUrl,
                name = dino.getLocalizedName(language),
                era = dino.era,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dino.getLocalizedName(language),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (dino.isFeatured) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = stringResource(R.string.featured),
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = dino.scientificName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dino.periodYearsAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

@Composable
private fun DinoImageOrPlaceholder(
    imageUrl: String?,
    name: String,
    era: DinosaurEra,
    modifier: Modifier = Modifier
) {
    val placeholder = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(eraColor(era).copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                style = MaterialTheme.typography.headlineLarge,
                color = eraColor(era)
            )
        }
    }
    if (!imageUrl.isNullOrBlank()) {
        val context = LocalContext.current
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = name,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            loading = { placeholder() },
            error = { placeholder() }
        )
    } else {
        Box(
            modifier = modifier.background(eraColor(era).copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                style = MaterialTheme.typography.headlineLarge,
                color = eraColor(era)
            )
        }
    }
}
