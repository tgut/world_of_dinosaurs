package com.example.world_of_dinosaurs_extented.domain.model

/**
 * Detailed information about geological periods with climate, flora, fauna, and major events.
 */
data class GeologicalPeriod(
    val era: DinosaurEra,
    val nameEn: String,
    val nameZh: String,
    val startMya: Int,
    val endMya: Int,
    val climateEn: String,
    val climateZh: String,
    val atmosphereO2Percent: Double,
    val atmosphereCO2Ppm: Int,
    val averageTempC: Int,
    val floraDominantEn: List<String>,
    val floraDominantZh: List<String>,
    val faunaPredecessorsEn: List<String>,  // 前驱生物
    val faunaPredecessorsZh: List<String>,
    val faunaContemporaryEn: List<String>,  // 同期生物
    val faunaContemporaryZh: List<String>,
    val faunaSuccessorsEn: List<String>,    // 后继生物
    val faunaSuccessorsZh: List<String>,
    val majorEventsEn: List<String>,
    val majorEventsZh: List<String>,
    val extinctionEventEn: String?,
    val extinctionEventZh: String?
) {
    fun getLocalizedName(language: String): String =
        if (language == "zh") nameZh else nameEn

    fun getLocalizedClimate(language: String): String =
        if (language == "zh") climateZh else climateEn

    fun getLocalizedFloraDominant(language: String): List<String> =
        if (language == "zh") floraDominantZh else floraDominantEn

    fun getLocalizedFaunaPredecessors(language: String): List<String> =
        if (language == "zh") faunaPredecessorsZh else faunaPredecessorsEn

    fun getLocalizedFaunaContemporary(language: String): List<String> =
        if (language == "zh") faunaContemporaryZh else faunaContemporaryEn

    fun getLocalizedFaunaSuccessors(language: String): List<String> =
        if (language == "zh") faunaSuccessorsZh else faunaSuccessorsEn

    fun getLocalizedMajorEvents(language: String): List<String> =
        if (language == "zh") majorEventsZh else majorEventsEn

    fun getLocalizedExtinctionEvent(language: String): String? =
        if (language == "zh") extinctionEventZh else extinctionEventEn
}
