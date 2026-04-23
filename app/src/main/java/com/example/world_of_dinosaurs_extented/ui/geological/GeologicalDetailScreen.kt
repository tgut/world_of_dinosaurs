package com.example.world_of_dinosaurs_extented.ui.geological

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeologicalDetailScreen(
    era: DinosaurEra,
    onNavigateBack: () -> Unit,
    language: String = "en",
    viewModel: GeologicalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(era, language) {
        viewModel.loadPeriod(era, language)
    }

    val period = uiState.period
    val currentLanguage = uiState.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(period?.getLocalizedName(currentLanguage) ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.error ?: "Unknown error")
            }
        } else if (period != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Timeline header with era colors
                val eraColor = when (era) {
                    DinosaurEra.TRIASSIC -> Color(0xFF8D6E63)
                    DinosaurEra.JURASSIC -> Color(0xFF2E7D32)
                    DinosaurEra.CRETACEOUS -> Color(0xFF1565C0)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(eraColor)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            "${period.startMya} - ${period.endMya} Mya",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "(${period.startMya - period.endMya} million years)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Climate & Atmosphere section
                Section(title = if (currentLanguage == "zh") "气候与大气" else "Climate & Atmosphere") {
                    InfoItem(
                        label = if (currentLanguage == "zh") "气候" else "Climate",
                        value = period.getLocalizedClimate(currentLanguage)
                    )
                    InfoItem(
                        label = if (currentLanguage == "zh") "平均温度" else "Avg Temperature",
                        value = "${period.averageTempC}°C"
                    )
                    InfoItem(
                        label = if (currentLanguage == "zh") "大气氧含量" else "Atmospheric O₂",
                        value = "${period.atmosphereO2Percent}%"
                    )
                    InfoItem(
                        label = if (currentLanguage == "zh") "大气CO₂" else "Atmospheric CO₂",
                        value = "${period.atmosphereCO2Ppm} ppm"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Flora section
                Section(title = if (currentLanguage == "zh") "主要植物" else "Dominant Flora") {
                    PillChipRow(period.getLocalizedFloraDominant(currentLanguage))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fauna section - Predecessors
                Section(title = if (currentLanguage == "zh") "前驱生物（前一时期）" else "Predecessor Fauna (Previous Period)") {
                    PillChipRow(period.getLocalizedFaunaPredecessors(currentLanguage))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fauna section - Contemporary
                Section(title = if (currentLanguage == "zh") "同期生物（本时期）" else "Contemporary Fauna (This Period)") {
                    PillChipRow(period.getLocalizedFaunaContemporary(currentLanguage))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fauna section - Successors
                Section(title = if (currentLanguage == "zh") "后继生物（后一时期）" else "Successor Fauna (Next Period)") {
                    PillChipRow(period.getLocalizedFaunaSuccessors(currentLanguage))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Major Events section
                Section(title = if (currentLanguage == "zh") "主要事件" else "Major Events") {
                    period.getLocalizedMajorEvents(currentLanguage).forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", modifier = Modifier.padding(end = 8.dp, top = 2.dp))
                            Text(event, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Extinction event if applicable
                period.getLocalizedExtinctionEvent(currentLanguage)?.let { event ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFFFCDD2)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                if (currentLanguage == "zh") "⚠️ 灭绝事件" else "⚠️ Extinction Event",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                event,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PillChipRow(items: List<String>) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            AssistChip(
                onClick = { },
                label = { Text(item, fontSize = 12.sp) },
                modifier = Modifier.height(32.dp)
            )
        }
    }
}
