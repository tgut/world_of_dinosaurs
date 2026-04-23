package com.example.world_of_dinosaurs_extented.ui.timeline

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator

// Era colors
private val TriassicColor = Color(0xFF8D6E63)
private val JurassicColor = Color(0xFF2E7D32)
private val CretaceousColor = Color(0xFF1565C0)
private val ExtinctionColor = Color(0xFFD32F2F)
private val TodayColor = Color(0xFF43A047)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onEraClick: (DinosaurEra) -> Unit,
    onEraViewDetails: (DinosaurEra) -> Unit,
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
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // --- Header banner ---
                item(key = "header") {
                    TimelineHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Era items with gaps ---
                val eras = DinosaurEra.entries
                eras.forEachIndexed { index, era ->
                    // Gap label between eras
                    if (index > 0) {
                        val prevEra = eras[index - 1]
                        val gap = prevEra.endMya - era.startMya
                        item(key = "gap_${prevEra.name}_${era.name}") {
                            TimelineGapLabel(
                                gapYears = if (gap > 0) gap else era.startMya - prevEra.endMya,
                                topColor = eraColor(prevEra),
                                bottomColor = eraColor(era)
                            )
                        }
                    }

                    item(key = era.name) {
                        EraTimelineItem(
                            era = era,
                            dinosaurs = uiState.eraGroups[era] ?: emptyList(),
                            language = uiState.language,
                            onClick = { onEraClick(era) },
                            onViewDetails = { onEraViewDetails(era) }
                        )
                    }
                }

                // --- Extinction event ---
                item(key = "extinction_gap") {
                    TimelineGapLabel(
                        gapYears = 79, // 145 - 66
                        topColor = CretaceousColor,
                        bottomColor = ExtinctionColor
                    )
                }

                item(key = "extinction") {
                    ExtinctionMarker()
                }

                // --- Gap to today ---
                item(key = "today_gap") {
                    TimelineGapLabel(
                        gapYears = 66,
                        topColor = ExtinctionColor,
                        bottomColor = TodayColor
                    )
                }

                // --- Today node ---
                item(key = "today") {
                    TodayNode()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ─── Header Banner ───

@Composable
private fun TimelineHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            TriassicColor,
                            JurassicColor,
                            CretaceousColor,
                            TodayColor
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.timeline_header),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.timeline_header_range),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ─── Era Item ───

@Composable
private fun EraTimelineItem(
    era: DinosaurEra,
    dinosaurs: List<Dinosaur>,
    language: String,
    onClick: () -> Unit,
    onViewDetails: () -> Unit
) {
    val color = eraColor(era)
    val duration = era.startMya - era.endMya

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            // Node dot with border ring
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(3.dp, color, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        // Era card with left accent bar
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.06f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row {
                // Left accent bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(color)
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = eraLabel(era),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                            Text(
                                text = "${era.startMya} – ${era.endMya} Ma",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = color
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Era description
                    Text(
                        text = eraDescription(era),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = color.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Duration tag
                    Text(
                        text = stringResource(R.string.lasted_years, duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dinosaur count
                    Text(
                        text = stringResource(R.string.timeline_explore, dinosaurs.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (dinosaurs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(dinosaurs.take(8), key = { it.id }) { dino ->
                                DinoMiniAvatar(
                                    name = dino.getLocalizedName(language),
                                    eraColor = color
                                )
                            }
                            if (dinosaurs.size > 8) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(MaterialTheme.shapes.extraLarge)
                                            .background(color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+${dinosaurs.size - 8}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = color
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // View Details button
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = color
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, color)
                    ) {
                        Text(
                            text = stringResource(R.string.view_geological_details),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

// ─── Gap label between eras ───

@Composable
private fun TimelineGapLabel(
    gapYears: Int,
    topColor: Color,
    bottomColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gradient connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(topColor, bottomColor)
                        )
                    )
            )
        }
        // Gap duration text
        Text(
            text = stringResource(R.string.million_years_gap, gapYears),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// ─── Extinction Marker ───

@Composable
private fun ExtinctionMarker() {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Red node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .border(3.dp, ExtinctionColor, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(ExtinctionColor)
            )
        }

        // Extinction banner card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = ExtinctionColor.copy(alpha = 0.10f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(ExtinctionColor)
                )
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "☄ " + stringResource(R.string.mass_extinction),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ExtinctionColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.mass_extinction_time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ─── Today Node ───

@Composable
private fun TodayNode() {
    val infiniteTransition = rememberInfiniteTransition(label = "today_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing green node
        Box(
            modifier = Modifier.width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulse ring
            Box(
                modifier = Modifier
                    .size((22 * pulseScale).dp)
                    .clip(CircleShape)
                    .background(TodayColor.copy(alpha = pulseAlpha))
            )
            // Solid dot
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(3.dp, TodayColor, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(TodayColor)
            )
        }

        // Today text
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = stringResource(R.string.today) + " · 0 Ma",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TodayColor
            )
            Text(
                text = stringResource(R.string.you_are_here),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Mini Avatar ───

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

// ─── Helpers ───

@Composable
private fun eraLabel(era: DinosaurEra): String = when (era) {
    DinosaurEra.TRIASSIC -> stringResource(R.string.triassic)
    DinosaurEra.JURASSIC -> stringResource(R.string.jurassic)
    DinosaurEra.CRETACEOUS -> stringResource(R.string.cretaceous)
}

@Composable
private fun eraDescription(era: DinosaurEra): String = when (era) {
    DinosaurEra.TRIASSIC -> stringResource(R.string.era_triassic_desc)
    DinosaurEra.JURASSIC -> stringResource(R.string.era_jurassic_desc)
    DinosaurEra.CRETACEOUS -> stringResource(R.string.era_cretaceous_desc)
}

private fun eraColor(era: DinosaurEra): Color = when (era) {
    DinosaurEra.TRIASSIC -> TriassicColor
    DinosaurEra.JURASSIC -> JurassicColor
    DinosaurEra.CRETACEOUS -> CretaceousColor
}
