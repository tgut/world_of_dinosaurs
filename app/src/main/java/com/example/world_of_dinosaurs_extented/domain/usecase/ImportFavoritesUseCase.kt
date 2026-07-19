package com.example.world_of_dinosaurs_extented.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.world_of_dinosaurs_extented.data.local.dao.FavoriteDao
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

data class ImportResult(
    val importedCount: Int,
    val skippedCount: Int,
    val errors: List<String> = emptyList()
)

@Singleton
class ImportFavoritesUseCase @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(uri: Uri): ImportResult {
        val userId = userRepository.getUserId() ?: ""
        val jsonString = readJsonFromUri(uri)

        val json = JSONObject(jsonString)
        val favoritesArray = json.optJSONArray("favorites")
            ?: throw Exception("Invalid format: 'favorites' array not found")

        var imported = 0
        var skipped = 0
        val errors = mutableListOf<String>()

        for (i in 0 until favoritesArray.length()) {
            try {
                val item = favoritesArray.getJSONObject(i)
                val dinosaurId = item.optString("dinosaurId", "")
                if (dinosaurId.isBlank()) {
                    skipped++
                    continue
                }

                // Check if already favorited
                if (favoriteDao.isFavoriteSync(dinosaurId, userId)) {
                    skipped++
                    continue
                }

                favoriteDao.addFavorite(
                    FavoriteEntity(
                        dinosaurId = dinosaurId,
                        userId = userId,
                        addedAt = System.currentTimeMillis()
                    )
                )
                imported++
            } catch (e: Exception) {
                errors.add("Item $i: ${e.message}")
                skipped++
            }
        }

        return ImportResult(imported, skipped, if (errors.isEmpty()) emptyList() else errors)
    }

    private fun readJsonFromUri(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open selected file")
        return inputStream.bufferedReader().use { it.readText() }
    }
}
