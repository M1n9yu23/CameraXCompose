package com.bossmg.android.cameraxcompose.screen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CameraView(
    viewModel: CameraViewModel = viewModel()
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val coordinateTransformer = remember { MutableCoordinateTransformer() }
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest { event ->
            when (event) {
                is CameraEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is CameraEvent.OpenUrl -> {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                    } catch (e: Exception) {
                        Log.e("CameraView", e.message, e)
                        Toast.makeText(context, "URL 열기 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    CameraViewScreen(
        surfaceRequest = surfaceRequest,
        coordinateTransformer = coordinateTransformer,
        onTakePhoto = {
            viewModel.takePhoto(
                context.applicationContext,
                ContextCompat.getMainExecutor(context)
            )
        }
    )
}

@Composable
private fun CameraViewScreen(
    surfaceRequest: SurfaceRequest?,
    coordinateTransformer: MutableCoordinateTransformer?,
    onTakePhoto: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier.fillMaxSize()
            )
        }

        ShutterButton(
            {
                onTakePhoto()
            },
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 66.dp)
        )
    }
}

@Composable
private fun ShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier.clickable(onClick = { onClick() }, enabled = enabled),
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(color = if (enabled) White else Color.Gray, CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(88.dp)
                .background(White, CircleShape)
                .border(
                    width = 2.dp,
                    color = Color(0xFF656565),
                    shape = CircleShape
                )
        )
    }
}


@Preview
@Composable
fun CameraViewPreview() {
    CameraViewScreen(
        null,
        null
    ) {}
}