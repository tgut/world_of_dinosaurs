package com.example.world_of_dinosaurs_extented.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dinosaurs")
data class DinosaurEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameZh: String,
    val scientificName: String,
    val description: String,
    val descriptionZh: String,
    val era: String,
    val periodYearsAgo: String,
    val diet: String,
    val size: String,
    val lengthMeters: Double?,
    val weightKg: Double?,
    val heightMeters: Double?,
    val imageUrl: String?,
    val facts: String,
    val factsZh: String,
    val habitat: String,
    val habitatZh: String,
    val discoveryYear: Int?,
    val discoveryLocation: String,
    val model3dUrl: String?,
    val isFeatured: Boolean,
    val dataSource: String,
    val lastUpdated: Long
)
