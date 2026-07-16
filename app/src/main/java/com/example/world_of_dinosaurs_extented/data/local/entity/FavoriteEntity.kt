package com.example.world_of_dinosaurs_extented.data.local.entity

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["dinosaurId", "userId"])
data class FavoriteEntity(
    val dinosaurId: String,
    val userId: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
