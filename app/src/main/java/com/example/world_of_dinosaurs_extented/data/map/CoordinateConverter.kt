package com.example.world_of_dinosaurs_extented.data.map

import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Coordinate converter for WGS-84 and GCJ-02 coordinate systems
 *
 * WGS-84: International standard GPS coordinate system (used by OSM, Google Maps outside China)
 * GCJ-02: Chinese coordinate system (Mars coordinate system, used by Tencent Maps, Baidu Maps)
 */
object CoordinateConverter {

    private const val PI = 3.1415926535897932384626
    private const val AXIS = 6378245.0
    private const val OFFSET = 0.00669342162296594323

    /**
     * Check if a coordinate is in China
     */
    fun isInChina(lat: Double, lng: Double): Boolean {
        return lng >= 72.004 && lng <= 137.8347 && lat >= 0.8293 && lat <= 55.8271
    }

    /**
     * Check if a coordinate is in out of China
     */
    private fun isOutOfChina(lat: Double, lng: Double): Boolean {
        return !isInChina(lat, lng)
    }

    /**
     * Transform latitude to GCJ-02
     */
    private fun transformLat(x: Double, y: Double): Double {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(y * PI) + 40.0 * sin(y / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(y / 12.0 * PI) + 320.0 * sin(y * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    /**
     * Transform longitude to GCJ-02
     */
    private fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(x * PI) + 40.0 * sin(x / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(x / 12.0 * PI) + 300.0 * sin(x / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    /**
     * Convert WGS-84 to GCJ-02
     */
    fun wgs84ToGcj02(lat: Double, lng: Double): Pair<Double, Double> {
        if (isOutOfChina(lat, lng)) {
            return Pair(lat, lng)
        }
        var dLat = transformLat(lng - 105.0, lat - 35.0)
        var dLng = transformLon(lng - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * PI
        var magic = sin(radLat)
        magic = 1 - OFFSET * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = (dLat * 180.0) / ((AXIS * (1 - OFFSET)) / (magic * sqrtMagic) * PI)
        dLng = (dLng * 180.0) / (AXIS / sqrtMagic * cos(radLat) * PI)
        val mgLat = lat + dLat
        val mgLng = lng + dLng
        return Pair(mgLat, mgLng)
    }

    /**
     * Convert GCJ-02 to WGS-84
     */
    fun gcj02ToWgs84(lat: Double, lng: Double): Pair<Double, Double> {
        if (isOutOfChina(lat, lng)) {
            return Pair(lat, lng)
        }
        var dLat = transformLat(lng - 105.0, lat - 35.0)
        var dLng = transformLon(lng - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * PI
        var magic = sin(radLat)
        magic = 1 - OFFSET * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = (dLat * 180.0) / ((AXIS * (1 - OFFSET)) / (magic * sqrtMagic) * PI)
        dLng = (dLng * 180.0) / (AXIS / sqrtMagic * cos(radLat) * PI)
        val mgLat = lat + dLat
        val mgLng = lng + dLng
        return Pair(lat * 2 - mgLat, lng * 2 - mgLng)
    }

    /**
     * Convert WGS-84 to GCJ-02 (for display on Tencent Maps)
     */
    fun toGcj02(lat: Double, lng: Double, targetProvider: MapProvider): Pair<Double, Double> {
        return if (targetProvider == MapProvider.TENCENT) {
            wgs84ToGcj02(lat, lng)
        } else {
            Pair(lat, lng)
        }
    }

    /**
     * Convert from GCJ-02 to WGS-84 (for storage)
     */
    fun toWgs84(lat: Double, lng: Double, sourceProvider: MapProvider): Pair<Double, Double> {
        return if (sourceProvider == MapProvider.TENCENT) {
            gcj02ToWgs84(lat, lng)
        } else {
            Pair(lat, lng)
        }
    }
}