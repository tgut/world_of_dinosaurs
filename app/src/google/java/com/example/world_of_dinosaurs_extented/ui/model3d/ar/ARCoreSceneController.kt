package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.content.Context
import com.google.ar.core.ArCoreApk
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ARCoreSceneController @Inject constructor() : ARSceneController {

    override suspend fun checkAvailability(context: Context): Boolean {
        return try {
            var availability = ArCoreApk.getInstance().checkAvailability(context)
            while (availability.isTransient) {
                delay(200)
                availability = ArCoreApk.getInstance().checkAvailability(context)
            }
            availability == ArCoreApk.Availability.SUPPORTED_INSTALLED ||
                availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD ||
                availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED
        } catch (_: Exception) {
            false
        }
    }
}
