package com.example.world_of_dinosaurs_extented.data.map

object DiscoveryLocationCoordinates {

    data class LatLng(val lat: Double, val lng: Double)

    private val coordinates = mapOf(
        "Alberta, Canada" to LatLng(53.9, -116.6),
        "Arizona, USA" to LatLng(34.0, -111.1),
        "Bahariya Formation, Egypt" to LatLng(28.55, 29.0),
        "Bavaria, Germany" to LatLng(48.8, 11.5),
        "Colorado, USA" to LatLng(39.5, -105.8),
        "Denver, Colorado, USA" to LatLng(39.7, -104.9),
        "Dorset, England" to LatLng(50.7, -2.4),
        "Gobi Desert, Mongolia" to LatLng(43.5, 103.5),
        "Kansas, USA" to LatLng(38.5, -98.8),
        "La Rioja, Argentina" to LatLng(-29.4, -66.9),
        "Liaoning, China" to LatLng(41.3, 122.4),
        "Maastricht, Netherlands" to LatLng(50.85, 5.69),
        "Mahajanga, Madagascar" to LatLng(-15.7, 46.3),
        "Montana, USA" to LatLng(47.0, -109.6),
        "Morrison, Colorado, USA" to LatLng(39.65, -105.2),
        "New Mexico, USA" to LatLng(34.5, -106.0),
        "Nuremberg, Germany" to LatLng(49.45, 11.08),
        "Oxfordshire, England" to LatLng(51.75, -1.25),
        "Patagonia, Argentina" to LatLng(-43.0, -68.5),
        "San Juan, Argentina" to LatLng(-31.5, -68.5),
        "Shandong, China" to LatLng(36.3, 118.0),
        "Sichuan, China" to LatLng(30.6, 104.1),
        "Solnhofen, Germany" to LatLng(48.9, 11.0),
        "Sussex, England" to LatLng(50.9, -0.3),
        "Tendaguru, Tanzania" to LatLng(-10.0, 38.5),
        "Texas, USA" to LatLng(31.9, -99.9),
        "Thuringia, Germany" to LatLng(50.9, 11.0),
        "Wurttemberg, Germany" to LatLng(48.5, 9.2),
        "Wyoming, USA" to LatLng(43.0, -107.6)
    )

    fun lookup(location: String): LatLng? = coordinates[location]

    fun allLocations(): Set<String> = coordinates.keys
}
