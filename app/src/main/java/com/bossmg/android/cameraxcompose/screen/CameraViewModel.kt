package com.bossmg.android.cameraxcompose.screen

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class CameraViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val _event = MutableSharedFlow<ShowToast>()
    val event = _event.asSharedFlow()

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    private val imageCaptureUseCase =
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_FRONT_CAMERA, cameraPreviewUseCase, imageCaptureUseCase
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    fun takePhoto(context: Context, cameraExecutor: Executor) {
        val file = "camerax_compose${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CameraXSampleApp")
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCaptureUseCase.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e(TAG, "${error.message}", error)
                    viewModelScope.launch {
                        _event.emit(ShowToast("사진 저장 실패"))
                    }
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        _event.emit(ShowToast("사진 저장 완료!"))
                    }
                }
            })
    }

    companion object {
        private const val TAG = "CameraViewModel"
    }

}

data class ShowToast(val message: String)