package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.content.Context
import com.huawei.hiar.AREnginesApk
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AREngineSceneController @Inject constructor() : ARSceneController {

    override suspend fun checkAvailability(context: Context): ARAvailability {
        return try {
            var ready = AREnginesApk.isAREngineApkReady(context)
            var retries = 0
            while (!ready && retries < 5) {
                delay(300)
                ready = AREnginesApk.isAREngineApkReady(context)
                retries++
            }
            if (ready) ARAvailability.Ready else ARAvailability.NeedsInstall
        } catch (_: Exception) {
            ARAvailability.Unsupported
        }
    }
}
