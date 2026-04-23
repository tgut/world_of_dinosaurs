package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.GeologicalPeriod

interface GeologicalPeriodRepository {
    suspend fun getPeriodByEra(era: DinosaurEra): GeologicalPeriod?
    suspend fun getAllPeriods(): List<GeologicalPeriod>
}
