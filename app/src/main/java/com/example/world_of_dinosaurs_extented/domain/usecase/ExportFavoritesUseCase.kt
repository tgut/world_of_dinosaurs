package com.example.world_of_dinosaurs_extented.domain.usecase

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.world_of_dinosaurs_extented.data.local.dao.DinosaurDao
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

data class ExportResult(
    val filePath: String,
    val shareIntent: Intent
)

@Singleton
class ExportFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val dinosaurDao: DinosaurDao,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(): ExportResult {
        // Collect favorite IDs
        val favoriteIds = mutableListOf<String>()
        favoriteRepository.getFavoriteIds().collect { ids ->
            favoriteIds.addAll(ids)
            return@collect
        }

        if (favoriteIds.isEmpty()) {
            throw Exception("No favorites to export")
        }

        // Get dinosaur details
        val dinosaurs = dinosaurDao.getDinosaursByIds(favoriteIds)
        val userId = userRepository.getUserId() ?: ""

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dateFormatFile = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Build JSON with org.json
        val favoritesArray = JSONArray()
        dinosaurs.forEach { dino ->
            favoritesArray.put(JSONObject().apply {
                put("dinosaurId", dino.id)
                put("name", dino.name)
                put("nameZh", dino.nameZh)
                put("scientificName", dino.scientificName)
                put("era", dino.era)
                put("diet", dino.diet)
                put("periodYearsAgo", dino.periodYearsAgo)
            })
        }

        val exportJson = JSONObject().apply {
            put("exportedAt", dateFormat.format(Date()))
            put("appVersion", "1.1")
            put("user", JSONObject().apply {
                put("id", userId)
            })
            put("favorites", favoritesArray)
        }

        val jsonString = exportJson.toString(2) // pretty print

        // Write to Downloads directory
        val fileName = "WoD_favorites_${dateFormatFile.format(Date())}.json"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        downloadsDir.mkdirs()
        val file = File(downloadsDir, fileName)
        file.writeText(jsonString)

        // Create share intent
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return ExportResult(
            filePath = file.absolutePath,
            shareIntent = shareIntent
        )
    }
}