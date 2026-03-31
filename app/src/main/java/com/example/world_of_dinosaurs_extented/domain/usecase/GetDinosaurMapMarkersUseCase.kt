package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.data.map.DiscoveryLocationCoordinates
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurMapMarker
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDinosaurMapMarkersUseCase @Inject constructor(
    private val dinosaurRepository: DinosaurRepository
) {
    operator fun invoke(): Flow<List<DinosaurMapMarker>> {
        return dinosaurRepository.getDinosaurs().map { dinosaurs ->
            dinosaurs.mapNotNull { dino ->
                if (dino.discoveryLocation.isBlank()) return@mapNotNull null
                val coords = DiscoveryLocationCoordinates.lookup(dino.discoveryLocation)
                    ?: return@mapNotNull null
                DinosaurMapMarker(
                    dinosaurId = dino.id,
                    name = dino.name,
                    nameZh = dino.nameZh,
                    era = dino.era,
                    discoveryLocation = dino.discoveryLocation,
                    lat = coords.lat,
                    lng = coords.lng
                )
            }
        }
    }
}
