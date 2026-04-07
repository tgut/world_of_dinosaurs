package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.content.Context

/**
 * Flavor-agnostic AR availability checker.
 * - google flavor: uses ArCoreApk.checkAvailability()
 * - huawei flavor: uses AREnginesApk.isAREngineApkReady()
 */
interface ARSceneController {
    /**
     * Check whether the device supports AR and the required runtime is installed.
     *
     * Returns:
     *  [ARAvailability.Ready]          – AR is fully available, proceed to the scene.
     *  [ARAvailability.NeedsInstall]   – Device supports AR but runtime not installed/outdated.
     *                                    Implementation should have already triggered the
     *                                    install dialog; caller should show an "install" prompt.
     *  [ARAvailability.Unsupported]    – Device does not support AR.
     */
    suspend fun checkAvailability(context: Context): ARAvailability
}

enum class ARAvailability {
    Ready,
    NeedsInstall,
    Unsupported
}
