package com.bossmg.android.cameraxcompose.screen

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCode(
    private val onCodeScan: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImg = image.image ?: return image.close()
        val img = InputImage.fromMediaImage(mediaImg, image.imageInfo.rotationDegrees)

        scanner.process(img)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    barcode.rawValue?.let { onCodeScan(it) }
                }
            }
            .addOnCompleteListener { image.close() }
    }
}