package com.example.world_of_dinosaurs_extented.data.map

/**
 * Map provider enum for dual market support
 * - OSM_FRANCE: Used for international users (current implementation)
 * - TENCENT: Used for China domestic users
 * - AUTO: Automatically detect based on network connectivity
 */
enum class MapProvider(val key: String, val displayName: String) {
    AUTO("auto", "自动检测"),
    OSM_FRANCE("osm_france", "OSM 国际"),
    TENCENT("tencent", "腾讯地图");

    companion object {
        fun fromKey(key: String): MapProvider = entries.find { it.key == key } ?: AUTO
    }
}