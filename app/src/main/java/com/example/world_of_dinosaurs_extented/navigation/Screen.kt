package com.example.world_of_dinosaurs_extented.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home?era={era}") {
        fun createRoute(era: String? = null) = if (era != null) "home?era=$era" else "home"
    }
    data object Detail : Screen("detail/{dinosaurId}") {
        fun createRoute(dinosaurId: String) = "detail/$dinosaurId"
    }
    data object Favorites : Screen("favorites")
    data object Quiz : Screen("quiz")
    data object Model3D : Screen("model3d/{dinosaurId}") {
        fun createRoute(dinosaurId: String) = "model3d/$dinosaurId"
    }
    data object AR : Screen("ar/{dinosaurId}") {
        fun createRoute(dinosaurId: String) = "ar/$dinosaurId"
    }
    data object Settings : Screen("settings")
    data object Timeline : Screen("timeline")
    data object QrScan : Screen("qrscan")
    data object ScanHistory : Screen("scan_history")
    data object ReviewQuiz : Screen("review_quiz")
}
