package com.example.world_of_dinosaurs_extented.data.model3d

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    private val cacheDir: File
        get() = File(context.filesDir, "models").also { it.mkdirs() }

    /**
     * Resolves a model to a local file path, downloading if necessary.
     * Returns the file path if available, null otherwise.
     */
    suspend fun resolveModel(modelInfo: Model3dConfig.ModelInfo): String? {
        // Check if it's a bundled asset
        modelInfo.assetPath?.let { assetPath ->
            return "file:///android_asset/$assetPath"
        }

        // Check if it's a remote model
        modelInfo.remoteUrl?.let { url ->
            val cachedFile = File(cacheDir, "${modelInfo.dinosaurId}.glb")
            if (cachedFile.exists()) {
                return cachedFile.absolutePath
            }
            // Download
            return downloadModel(url, cachedFile)
        }

        return null
    }

    fun isModelCached(dinosaurId: String): Boolean {
        return File(cacheDir, "$dinosaurId.glb").exists()
    }

    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private suspend fun downloadModel(url: String, targetFile: File): String? =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.byteStream()?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    targetFile.absolutePath
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
}
