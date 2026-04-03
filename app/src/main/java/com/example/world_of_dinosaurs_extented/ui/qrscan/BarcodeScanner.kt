package com.example.world_of_dinosaurs_extented.ui.qrscan

import android.content.Context
import android.net.Uri

/**
 * Flavor-agnostic barcode scanner interface.
 * - google flavor: Google ML Kit
 * - huawei flavor: HMS ML Kit
 */
interface BarcodeScanner {
    /**
     * Scan a QR code from a gallery image URI.
     * [onSuccess] receives the raw QR string value.
     * [onNoQrFound] called when image contains no QR code.
     * [onFailure] called on any error.
     */
    fun scanFromUri(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onNoQrFound: () -> Unit,
        onFailure: (String) -> Unit
    )
}
