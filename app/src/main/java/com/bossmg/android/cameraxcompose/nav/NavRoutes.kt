package com.bossmg.android.cameraxcompose.nav

sealed class NavRoutes(val route: String) {
    object CameraPermission: NavRoutes("CameraPermission")
    object CameraView: NavRoutes("CameraView")
}