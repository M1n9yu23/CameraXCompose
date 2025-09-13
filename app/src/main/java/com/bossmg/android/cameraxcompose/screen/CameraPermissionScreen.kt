package com.bossmg.android.cameraxcompose.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun CameraPermission(
    goCameraViewScreen: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current

    var dialog by remember { mutableStateOf(false) }

    val onDialog: (Boolean) -> Unit = {
        dialog = it
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    "카메라 권한 허용"
                )
            },
            text = {
                Text(
                    "카메라 기능을 사용하기 때문에 카메라 권한을 허용 하셔야 앱을 사용하실 수 있어요!!"
                )
            },
            dismissButton = {
                Button(onClick = { dialog = false }) {
                    Text("취소")
                }
            },
            confirmButton = {
                Button(onClick = {
                    dialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text("권한 허용")
                }
            }
        )
    }

    CameraPermissionScreen(
        isCameraPermissionGranted = cameraPermissionState.status.isGranted,
        shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
        onDialog = { onDialog(it) },
        goCameraViewScreen = { goCameraViewScreen() }
    ) {
        cameraPermissionState.launchPermissionRequest()
    }
}

@Composable
private fun CameraPermissionScreen(
    isCameraPermissionGranted: Boolean,
    shouldShowRationale: Boolean,
    onDialog: (Boolean) -> Unit,
    goCameraViewScreen: () -> Unit,
    launchPermissionRequest: () -> Unit
) {
    if (isCameraPermissionGranted) {
        goCameraViewScreen()
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "안녕하세요! \n" +
                        "카메라 권한을 허용하고 앱의 기능을 자유롭게 이용하세요!",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (shouldShowRationale) {
                        onDialog(true)
                    } else {
                        launchPermissionRequest()
                    }
                }
            ) {
                Text("카메라를 풀어주세요!")
            }
        }

    }
}

@Preview
@Composable
fun CameraPermissionScreenPreview() {
    CameraPermissionScreen(
        false,
        false,
        {},
        {},
        {}
    )
}