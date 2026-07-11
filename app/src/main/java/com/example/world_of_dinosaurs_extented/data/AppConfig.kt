package com.example.world_of_dinosaurs_extented.data

/**
 * Application configuration constants
 *
 * WARNING: Secret values (Tencent Cloud keys) must NOT be hardcoded here.
 * They are provided via:
 * 1. Environment variables (TENCENT_SECRET_ID, TENCENT_SECRET_KEY)
 * 2. BuildConfig fields from local.properties
 * 3. Runtime user input in Settings screen
 *
 * The SettingsManager will return empty strings as defaults.
 */
object AppConfig {
    // Leave empty - secrets must be configured via Settings UI or environment
    const val TENCENT_SECRET_ID = ""
    const val TENCENT_SECRET_KEY = ""
}