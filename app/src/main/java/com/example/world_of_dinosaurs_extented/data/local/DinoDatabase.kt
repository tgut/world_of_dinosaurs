package com.example.world_of_dinosaurs_extented.data.local

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.world_of_dinosaurs_extented.data.local.dao.*
import com.example.world_of_dinosaurs_extented.data.local.entity.*

@Database(
    entities = [
        FavoriteEntity::class,
        ScanHistoryEntity::class,
        DinosaurEntity::class,
        UserEntity::class,
        ChatSessionEntity::class,
        ChatMessageEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DinoDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun dinosaurDao(): DinosaurDao
    abstract fun userDao(): UserDao
    abstract fun chatHistoryDao(): ChatHistoryDao

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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `users` (" +
                        "`id` TEXT NOT NULL PRIMARY KEY, " +
                        "`provider` TEXT NOT NULL, " +
                        "`providerId` TEXT NOT NULL, " +
                        "`displayName` TEXT, " +
                        "`avatarUrl` TEXT, " +
                        "`email` TEXT, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL)"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Recreate favorites with composite key + userId
                db.execSQL("CREATE TABLE IF NOT EXISTS `favorites_new` (" +
                    "`dinosaurId` TEXT NOT NULL, " +
                    "`userId` TEXT NOT NULL DEFAULT '', " +
                    "`addedAt` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`dinosaurId`, `userId`))")
                db.execSQL("INSERT INTO favorites_new (dinosaurId, userId, addedAt) " +
                    "SELECT dinosaurId, '', addedAt FROM favorites")
                db.execSQL("DROP TABLE favorites")
                db.execSQL("ALTER TABLE favorites_new RENAME TO favorites")

                // Add userId to scan_history
                db.execSQL("CREATE TABLE IF NOT EXISTS `scan_history_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`dinosaurId` TEXT NOT NULL, " +
                    "`userId` TEXT NOT NULL DEFAULT '', " +
                    "`scannedAt` INTEGER NOT NULL)")
                db.execSQL("INSERT INTO scan_history_new (id, dinosaurId, userId, scannedAt) " +
                    "SELECT id, dinosaurId, '', scannedAt FROM scan_history")
                db.execSQL("DROP TABLE scan_history")
                db.execSQL("ALTER TABLE scan_history_new RENAME TO scan_history")

                // New tables for chat history
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `chat_sessions` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`userId` TEXT NOT NULL DEFAULT '', " +
                        "`title` TEXT NOT NULL DEFAULT 'New Chat', " +
                        "`dinosaurId` TEXT, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL)")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `chat_messages` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`sessionId` INTEGER NOT NULL, " +
                        "`role` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)")
            }
        }
    }
}
