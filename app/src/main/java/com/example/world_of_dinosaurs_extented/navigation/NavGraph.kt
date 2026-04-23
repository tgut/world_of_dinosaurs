package com.example.world_of_dinosaurs_extented.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.ui.chat.ChatScreen
import com.example.world_of_dinosaurs_extented.ui.detail.DetailScreen
import com.example.world_of_dinosaurs_extented.ui.favorites.FavoritesScreen
import com.example.world_of_dinosaurs_extented.ui.geological.GeologicalDetailScreen
import com.example.world_of_dinosaurs_extented.ui.home.HomeScreen
import com.example.world_of_dinosaurs_extented.ui.model3d.ARViewScreen
import com.example.world_of_dinosaurs_extented.ui.model3d.Model3DScreen
import com.example.world_of_dinosaurs_extented.ui.quiz.QuizScreen
import com.example.world_of_dinosaurs_extented.ui.qrscan.QrScanScreen
import com.example.world_of_dinosaurs_extented.ui.scanhistory.ScanHistoryScreen
import com.example.world_of_dinosaurs_extented.ui.reviewquiz.ReviewQuizScreen
import com.example.world_of_dinosaurs_extented.ui.recognition.DinoRecognitionScreen
import com.example.world_of_dinosaurs_extented.ui.map.DiscoveryMapScreen
import com.example.world_of_dinosaurs_extented.ui.chat.ChatScreen
import com.example.world_of_dinosaurs_extented.ui.settings.SettingsScreen
import com.example.world_of_dinosaurs_extented.ui.timeline.TimelineScreen

@Composable
fun DinoNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.createRoute()) {
        composable(
            Screen.Home.route,
            arguments = listOf(navArgument("era") { type = NavType.StringType; defaultValue = "" })
        ) {
            HomeScreen(
                onDinosaurClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToTimeline = { navController.navigate(Screen.Timeline.route) },
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToRecognition = { navController.navigate(Screen.DinoRecognition.route) },
                onNavigateToMap = { navController.navigate(Screen.DiscoveryMap.createRoute()) },
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute()) }
            )
        }
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("dinosaurId") { type = NavType.StringType })
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onView3D = { id -> navController.navigate(Screen.Model3D.createRoute(id)) },
                onViewAR = { id -> navController.navigate(Screen.AR.createRoute(id)) },
                onViewOnMap = { id -> navController.navigate(Screen.DiscoveryMap.createRoute(id)) },
                onAskAI = { id -> navController.navigate(Screen.Chat.createRoute(id)) }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onDinosaurClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Quiz.route) {
            QuizScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            Screen.Model3D.route,
            arguments = listOf(navArgument("dinosaurId") { type = NavType.StringType })
        ) {
            Model3DScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAR = null  // AR feature temporarily hidden
            )
        }
        composable(
            Screen.AR.route,
            arguments = listOf(navArgument("dinosaurId") { type = NavType.StringType })
        ) {
            ARViewScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Timeline.route) {
            TimelineScreen(
                onEraClick = { era ->
                    navController.navigate(Screen.Home.createRoute(era.name))
                },
                onEraViewDetails = { era ->
                    navController.navigate(Screen.GeologicalDetail.createRoute(era.name))
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.createRoute()) },
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute()) }
            )
        }
        composable(Screen.QrScan.route) {
            QrScanScreen(
                onDinosaurScanned = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToHistory = { navController.navigate(Screen.ScanHistory.route) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.createRoute()) },
                onNavigateToTimeline = { navController.navigate(Screen.Timeline.route) },
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute()) }
            )
        }
        composable(Screen.ScanHistory.route) {
            ScanHistoryScreen(
                onDinosaurClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onStartReviewQuiz = { navController.navigate(Screen.ReviewQuiz.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ReviewQuiz.route) {
            ReviewQuizScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.DinoRecognition.route) {
            DinoRecognitionScreen(
                onDinosaurClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.DiscoveryMap.route,
            arguments = listOf(navArgument("dinosaurId") { type = NavType.StringType; defaultValue = "" })
        ) {
            DiscoveryMapScreen(
                onDinosaurClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Chat.route,
            arguments = listOf(navArgument("dinosaurId") { type = NavType.StringType; defaultValue = "" })
        ) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.GeologicalDetail.route,
            arguments = listOf(navArgument("era") { type = NavType.StringType })
        ) { backStackEntry ->
            val eraString = backStackEntry.arguments?.getString("era") ?: "TRIASSIC"
            val era = try {
                DinosaurEra.valueOf(eraString)
            } catch (e: IllegalArgumentException) {
                DinosaurEra.TRIASSIC
            }
            GeologicalDetailScreen(
                era = era,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
