package com.example.world_of_dinosaurs_extented.ui.qrscan

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitBarcodeScanner @Inject constructor() : BarcodeScanner {

    override fun scanFromUri(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onNoQrFound: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val value = barcodes.firstOrNull()?.rawValue
                    if (value != null) onSuccess(value) else onNoQrFound()
                }
                .addOnFailureListener { e ->
                    onFailure(e.message ?: "Failed to scan image")
                }
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to load image")
        }
    }
}
