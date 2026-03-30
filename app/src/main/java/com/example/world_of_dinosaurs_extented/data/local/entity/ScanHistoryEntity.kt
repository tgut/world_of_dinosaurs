package com.example.world_of_dinosaurs_extented.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dinosaurId: String,
    val scannedAt: Long = System.currentTimeMillis()
)
