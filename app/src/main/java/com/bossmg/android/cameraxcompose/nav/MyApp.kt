package com.bossmg.android.cameraxcompose.nav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bossmg.android.cameraxcompose.screen.CameraPermission
import com.bossmg.android.cameraxcompose.screen.CameraView

@Composable
fun App() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.CameraPermission.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(NavRoutes.CameraPermission.route) {
                CameraPermission {
                    navController.navigate(NavRoutes.CameraView.route) {
                        popUpTo(NavRoutes.CameraPermission.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(NavRoutes.CameraView.route) {
                CameraView()
            }

        }
    }
}