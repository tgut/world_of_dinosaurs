package com.example.world_of_dinosaurs_extented.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.world_of_dinosaurs_extented.data.local.dao.DinosaurDao
import com.example.world_of_dinosaurs_extented.data.local.dao.FavoriteDao
import com.example.world_of_dinosaurs_extented.data.local.dao.ScanHistoryDao
import com.example.world_of_dinosaurs_extented.data.local.entity.DinosaurEntity
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import com.example.world_of_dinosaurs_extented.data.local.entity.ScanHistoryEntity

@Database(
    entities = [FavoriteEntity::class, ScanHistoryEntity::class, DinosaurEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DinoDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun dinosaurDao(): DinosaurDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `scan_history` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`dinosaurId` TEXT NOT NULL, " +
                        "`scannedAt` INTEGER NOT NULL)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `dinosaurs` (" +
                        "`id` TEXT NOT NULL PRIMARY KEY, " +
                        "`name` TEXT NOT NULL, " +
                        "`nameZh` TEXT NOT NULL, " +
                        "`scientificName` TEXT NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`descriptionZh` TEXT NOT NULL, " +
                        "`era` TEXT NOT NULL, " +
                        "`periodYearsAgo` TEXT NOT NULL, " +
                        "`diet` TEXT NOT NULL, " +
                        "`size` TEXT NOT NULL, " +
                        "`lengthMeters` REAL, " +
                        "`weightKg` REAL, " +
                        "`heightMeters` REAL, " +
                        "`imageUrl` TEXT, " +
                        "`facts` TEXT NOT NULL, " +
                        "`factsZh` TEXT NOT NULL, " +
                        "`habitat` TEXT NOT NULL, " +
                        "`habitatZh` TEXT NOT NULL, " +
                        "`discoveryYear` INTEGER, " +
                        "`discoveryLocation` TEXT NOT NULL, " +
                        "`model3dUrl` TEXT, " +
                        "`isFeatured` INTEGER NOT NULL DEFAULT 0, " +
                        "`dataSource` TEXT NOT NULL DEFAULT 'bundled', " +
                        "`lastUpdated` INTEGER NOT NULL DEFAULT 0)"
                )
            }
        }
    }
}
