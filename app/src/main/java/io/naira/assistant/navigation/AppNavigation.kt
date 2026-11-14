package io.naira.assistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.naira.assistant.presentation.auth.screen.LoginScreen
import io.naira.assistant.presentation.auth.screen.RegisterScreen
import io.naira.assistant.presentation.auth.screen.ResetPasswordScreen
import io.naira.assistant.presentation.main.screen.MainScreen
import io.naira.assistant.presentation.splash.screen.SplashScreen
import io.naira.assistant.services.auth.FirebaseAuthService

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authService = remember { FirebaseAuthService() }
    val navigateToMainFromSplash: () -> Unit = {
        val destination = if (authService.hasCurrentUser) {
            AppDestinations.MAIN
        } else {
            AppDestinations.LOGIN
        }
        navController.navigate(destination) {
            popUpTo(AppDestinations.SPLASH) { inclusive = true }
            launchSingleTop = true
        }
    }
    val navigateToMainFromLogin: () -> Unit = {
        navController.navigate(AppDestinations.MAIN) {
            popUpTo(AppDestinations.LOGIN) { inclusive = true }
            launchSingleTop = true
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
                onContinueOffline = navigateToMainFromSplash,
                onNavigateToMain = navigateToMainFromSplash,
            )
        }

        composable(AppDestinations.MAIN) {
            MainScreen()
        }

        composable(AppDestinations.LOGIN) {
            LoginScreen(
                authService = authService,
                onLoggedIn = navigateToMainFromLogin,
                onRegisterClick = { navController.navigate(AppDestinations.REGISTER) },
                onForgotPasswordClick = { navController.navigate(AppDestinations.RESET_PASSWORD) },
            )
        }

        composable(AppDestinations.REGISTER) {
            RegisterScreen(
                authService = authService,
                onBack = { navController.popBackStack() },
                onRegistered = navigateToMainFromLogin,
                onLogin = { navController.popBackStack() },
            )
        }

        composable(AppDestinations.RESET_PASSWORD) {
            ResetPasswordScreen(
                authService = authService,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
