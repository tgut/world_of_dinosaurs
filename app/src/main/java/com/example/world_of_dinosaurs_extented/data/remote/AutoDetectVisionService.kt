package com.example.world_of_dinosaurs_extented.data.remote

import android.app.Application
import com.example.world_of_dinosaurs_extented.data.remote.dto.LabelAnnotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auto-detect Vision service provider based on network connectivity
 * Tries Google Vision first, falls back to Tencent Cloud if Google is not accessible
 */
@Singleton
class AutoDetectVisionService @Inject constructor(
    private val googleVisionService: GoogleVisionService,
    private val tencentVisionService: TencentVisionService,
    private val application: Application
) : VisionService {

    private var lastDetectionTime = 0L
    private var cachedProvider: VisionServiceProvider = VisionServiceProvider.UNKNOWN

    private enum class VisionServiceProvider {
        GOOGLE,
        TENCENT,
        UNKNOWN
    }

    override suspend fun analyzeImage(base64Image: String): List<LabelAnnotation> {
        val provider = detectProvider()
        return when (provider) {
            VisionServiceProvider.GOOGLE -> googleVisionService.analyzeImage(base64Image)
            VisionServiceProvider.TENCENT -> tencentVisionService.analyzeImage(base64Image)
            VisionServiceProvider.UNKNOWN -> {
                // Try both and return first successful result
                try {
                    googleVisionService.analyzeImage(base64Image)
                } catch (e: Exception) {
                    tencentVisionService.analyzeImage(base64Image)
                }
            }
        }
    }

    private suspend fun detectProvider(): VisionServiceProvider {
        // Use cached result if valid (30 minutes)
        val cacheDuration = 30 * 60 * 1000L
        if (cachedProvider != VisionServiceProvider.UNKNOWN &&
            System.currentTimeMillis() - lastDetectionTime < cacheDuration) {
            return cachedProvider
        }

        return withContext(Dispatchers.IO) {
            val googleAvailable = testGoogleConnectivity()
            cachedProvider = if (googleAvailable) {
                VisionServiceProvider.GOOGLE
            } else {
                VisionServiceProvider.TENCENT
            }
            lastDetectionTime = System.currentTimeMillis()
            cachedProvider
        }
    }

    /**
     * Test if Google Vision API is accessible
     */
    private suspend fun testGoogleConnectivity(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(3000) {
                    val url = URL("https://vision.googleapis.com/")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 3000
                    connection.readTimeout = 3000
                    connection.requestMethod = "HEAD"
                    connection.setRequestProperty("User-Agent", getUserAgent())
                    val responseCode = connection.responseCode
                    connection.disconnect()
                    responseCode in 200..499 // 400-499 means server is reachable but auth required
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun getUserAgent(): String {
        return "${application.packageName}/${getVersionName()} (Android)"
    }

    private fun getVersionName(): String {
        return try {
            val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}