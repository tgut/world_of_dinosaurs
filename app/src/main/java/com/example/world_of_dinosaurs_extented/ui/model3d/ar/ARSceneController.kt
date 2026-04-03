package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.content.Context

/**
 * Flavor-agnostic AR availability checker.
 * - google flavor: uses ArCoreApk.checkAvailability()
 * - huawei flavor: uses AREnginesApk.checkAvailability()
 */
interface ARSceneController {
    /**
     * Check whether the device supports AR.
     * Returns true if AR is available (or can be installed), false otherwise.
     */
    suspend fun checkAvailability(context: Context): Boolean
}
