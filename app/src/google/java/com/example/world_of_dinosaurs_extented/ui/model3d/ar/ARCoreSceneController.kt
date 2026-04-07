package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.app.Activity
import android.content.Context
import com.google.ar.core.ArCoreApk
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ARCoreSceneController @Inject constructor() : ARSceneController {

    override suspend fun checkAvailability(context: Context): ARAvailability {
        return try {
            var availability = ArCoreApk.getInstance().checkAvailability(context)
            var polls = 0
            while (availability.isTransient && polls < 10) {
                delay(200)
                availability = ArCoreApk.getInstance().checkAvailability(context)
                polls++
            }

            when (availability) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> ARAvailability.Ready

                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED,
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> {
                    // Trigger the Play Store install/update dialog
                    if (context is Activity) {
                        try {
                            ArCoreApk.getInstance().requestInstall(context, true)
                        } catch (_: Exception) { /* user cancelled */ }
                    }
                    ARAvailability.NeedsInstall
                }

                else -> ARAvailability.Unsupported
            }
        } catch (_: Exception) {
            ARAvailability.Unsupported
        }
    }
}
