package com.example.world_of_dinosaurs_extented.di

import android.content.Context
import androidx.room.Room
import com.example.world_of_dinosaurs_extented.data.local.DinoDatabase
import com.example.world_of_dinosaurs_extented.data.local.dao.DinosaurDao
import com.example.world_of_dinosaurs_extented.data.local.dao.FavoriteDao
import com.example.world_of_dinosaurs_extented.data.local.dao.ScanHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DinoDatabase {
        return Room.databaseBuilder(
            context,
            DinoDatabase::class.java,
            "dino_database"
        )
            .addMigrations(DinoDatabase.MIGRATION_1_2, DinoDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideFavoriteDao(database: DinoDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideScanHistoryDao(database: DinoDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }

    @Provides
    fun provideDinosaurDao(database: DinoDatabase): DinosaurDao {
        return database.dinosaurDao()
    }
}
