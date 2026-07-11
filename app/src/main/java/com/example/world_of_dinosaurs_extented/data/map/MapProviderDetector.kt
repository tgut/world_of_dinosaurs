package com.example.world_of_dinosaurs_extented.data.map

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL

/**
 * Detector for map service availability
 * Tests connectivity to OSM France and Tencent Maps servers
 */
class MapProviderDetector(private val context: Context) {

    /**
     * Test if OSM France servers are accessible
     */
    suspend fun isOsmFranceAvailable(): Boolean {
        return testConnection("https://a.tile.openstreetmap.fr/osmfr/0/0/0.png", 3000)
    }

    /**
     * Test if Tencent Maps servers are accessible
     */
    suspend fun isTencentAvailable(): Boolean {
        return testConnection("https://rt0.map.gtimg.com/realtimerender?z=0&x=0&y=0&type=vector&style=0", 3000)
    }

    /**
     * Auto-detect the best available map provider
     */
    suspend fun detectBestProvider(): MapProvider {
        return withContext(Dispatchers.IO) {
            val osmAvailable = isOsmFranceAvailable()

            if (osmAvailable) {
                MapProvider.OSM_FRANCE
            } else {
                MapProvider.TENCENT
            }
        }
    }

    /**
     * Test network connectivity to a URL
     */
    private suspend fun testConnection(urlString: String, timeoutMs: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(timeoutMs) {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = timeoutMs.toInt()
                    connection.readTimeout = timeoutMs.toInt()
                    connection.requestMethod = "HEAD"
                    connection.setRequestProperty("User-Agent", getUserAgent())
                    val responseCode = connection.responseCode
                    connection.disconnect()
                    responseCode in 200..399
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun getUserAgent(): String {
        return "${context.packageName}/${getVersionName()} (Android)"
    }

    private fun getVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    companion object {
        /**
         * Check if previously detected provider is still valid
         * Returns true if should re-detect
         */
        fun shouldRedetect(lastDetectionTime: Long, cacheDurationMs: Long = 24 * 60 * 60 * 1000L): Boolean {
            return System.currentTimeMillis() - lastDetectionTime > cacheDurationMs
        }
    }
}