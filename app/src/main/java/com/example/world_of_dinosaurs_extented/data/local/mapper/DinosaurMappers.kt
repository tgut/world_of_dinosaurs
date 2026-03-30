package com.example.world_of_dinosaurs_extented.data.local.mapper

import com.example.world_of_dinosaurs_extented.data.local.entity.DinosaurEntity
import com.example.world_of_dinosaurs_extented.data.remote.dto.DinosaurDto
import com.example.world_of_dinosaurs_extented.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private val moshi = Moshi.Builder().build()
private val listType = Types.newParameterizedType(List::class.java, String::class.java)
private val listAdapter = moshi.adapter<List<String>>(listType)

fun DinosaurDto.toEntity(dataSource: String = "remote"): DinosaurEntity = DinosaurEntity(
    id = id,
    name = name,
    nameZh = nameZh,
    scientificName = scientificName,
    description = description,
    descriptionZh = descriptionZh,
    era = era,
    periodYearsAgo = periodYearsAgo,
    diet = diet,
    size = size,
    lengthMeters = lengthMeters,
    weightKg = weightKg,
    heightMeters = heightMeters,
    imageUrl = imageUrl,
    facts = listAdapter.toJson(facts),
    factsZh = listAdapter.toJson(factsZh),
    habitat = habitat,
    habitatZh = habitatZh,
    discoveryYear = discoveryYear,
    discoveryLocation = discoveryLocation,
    model3dUrl = model3dUrl,
    isFeatured = isFeatured,
    dataSource = dataSource,
    lastUpdated = System.currentTimeMillis()
)

fun DinosaurEntity.toDomain(): Dinosaur = Dinosaur(
    id = id,
    name = name,
    nameZh = nameZh,
    scientificName = scientificName,
    description = description,
    descriptionZh = descriptionZh,
    era = try { DinosaurEra.valueOf(era) } catch (_: Exception) { DinosaurEra.CRETACEOUS },
    periodYearsAgo = periodYearsAgo,
    diet = try { DinosaurDiet.valueOf(diet) } catch (_: Exception) { DinosaurDiet.HERBIVORE },
    size = try { DinosaurSize.valueOf(size) } catch (_: Exception) { DinosaurSize.MEDIUM },
    lengthMeters = lengthMeters,
    weightKg = weightKg,
    heightMeters = heightMeters,
    imageUrl = imageUrl,
    facts = listAdapter.fromJson(facts) ?: emptyList(),
    factsZh = listAdapter.fromJson(factsZh) ?: emptyList(),
    habitat = habitat,
    habitatZh = habitatZh,
    discoveryYear = discoveryYear,
    discoveryLocation = discoveryLocation,
    model3dUrl = model3dUrl,
    isFavorite = false,
    isFeatured = isFeatured
)
