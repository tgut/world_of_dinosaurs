package com.example.world_of_dinosaurs_extented.data.map

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.MapTileIndex

/**
 * Tencent Maps tile sources
 * Tencent uses query parameters instead of path-based URLs
 */
object TencentTileSources {

    private const val API_KEY = "QKCBZ-5JT6G-SKQQD-QOC7K-34GOJ-P3FLE"

    /**
     * Custom tile source for Tencent Maps standard (vector) style
     */
    val STANDARD = object : OnlineTileSourceBase(
        "TencentStandard",
        0, 19, 256,
        ".png",
        arrayOf(
            "https://rt0.map.gtimg.com",
            "https://rt1.map.gtimg.com",
            "https://rt2.map.gtimg.com",
            "https://rt3.map.gtimg.com"
        )
    ) {
        override fun getTileURLString(tile: Long): String {
            val zoom = MapTileIndex.getZoom(tile)
            val x = MapTileIndex.getX(tile)
            val y = MapTileIndex.getY(tile)
            return baseUrl + "/realtimerender?z=$zoom&x=$x&y=$y&type=vector&style=0&key=$API_KEY"
        }
    }

    /**
     * Satellite Tencent Maps tile source
     */
    val SATELLITE = object : OnlineTileSourceBase(
        "TencentSatellite",
        0, 19, 256,
        ".jpg",
        arrayOf(
            "https://p0.map.gtimg.com",
            "https://p1.map.gtimg.com",
            "https://p2.map.gtimg.com",
            "https://p3.map.gtimg.com"
        )
    ) {
        override fun getTileURLString(tile: Long): String {
            val zoom = MapTileIndex.getZoom(tile)
            val x = MapTileIndex.getX(tile)
            val y = MapTileIndex.getY(tile)
            return baseUrl + "/sateTiles/$zoom/$x/$y.jpg?key=$API_KEY"
        }
    }

    fun getTileSource(isSatellite: Boolean) = if (isSatellite) SATELLITE else STANDARD
}