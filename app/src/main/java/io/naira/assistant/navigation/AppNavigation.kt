package io.naira.assistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.naira.assistant.presentation.home.screen.MainScreen
import io.naira.assistant.presentation.splash.screen.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navigateToMain: () -> Unit = {
        navController.navigate(AppDestinations.MAIN) {
            popUpTo(AppDestinations.SPLASH) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH
    ) {
        composable(AppDestinations.SPLASH) {
            SplashScreen(
                isChecking = false,
                isOffline = false,
                canContinueOffline = false,
                onRetry = {},
                onContinueOffline = navigateToMain,
                onNavigateToMain = navigateToMain,
            )
        }

        composable(AppDestinations.MAIN) {
            MainScreen()
        }
    }
}
