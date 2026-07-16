package com.example.world_of_dinosaurs_extented.ui.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra

@Composable
fun FilterChips(
    selectedEra: DinosaurEra?,
    selectedDiet: DinosaurDiet?,
    only3D: Boolean = false,
    onEraSelected: (DinosaurEra?) -> Unit,
    onDietSelected: (DinosaurDiet?) -> Unit,
    onToggle3D: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedEra == null,
                onClick = { onEraSelected(null) },
                label = { Text(stringResource(R.string.all)) }
            )
            DinosaurEra.entries.forEach { era ->
                val label = when (era) {
                    DinosaurEra.TRIASSIC -> stringResource(R.string.triassic)
                    DinosaurEra.JURASSIC -> stringResource(R.string.jurassic)
                    DinosaurEra.CRETACEOUS -> stringResource(R.string.cretaceous)
                }
                FilterChip(
                    selected = selectedEra == era,
                    onClick = { onEraSelected(if (selectedEra == era) null else era) },
                    label = { Text(label) }
                )
            }
        }
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = only3D,
                onClick = onToggle3D,
                label = { Text("3D") }
            )
            FilterChip(
                selected = selectedDiet == null,
                onClick = { onDietSelected(null) },
                label = { Text(stringResource(R.string.all)) }
            )
            DinosaurDiet.entries.forEach { diet ->
                val label = when (diet) {
                    DinosaurDiet.HERBIVORE -> stringResource(R.string.herbivore)
                    DinosaurDiet.CARNIVORE -> stringResource(R.string.carnivore)
                    DinosaurDiet.OMNIVORE -> stringResource(R.string.omnivore)
                    DinosaurDiet.PISCIVORE -> stringResource(R.string.piscivore)
                }
                FilterChip(
                    selected = selectedDiet == diet,
                    onClick = { onDietSelected(if (selectedDiet == diet) null else diet) },
                    label = { Text(label) }
                )
            }
        }
    }
}
