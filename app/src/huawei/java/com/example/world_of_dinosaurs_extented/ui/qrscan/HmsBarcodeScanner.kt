package com.example.world_of_dinosaurs_extented.ui.qrscan

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.ml.scan.HmsScan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HmsBarcodeScanner @Inject constructor() : BarcodeScanner {

    private val options = HmsScanAnalyzerOptions.Creator()
        .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
        .create()

    override fun scanFromUri(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onNoQrFound: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: run { onFailure("Failed to load image"); return }

            val results = ScanUtil.decodeWithBitmap(context, bitmap, options)
            val value = results?.firstOrNull()?.getOriginalValue()
            if (value != null) onSuccess(value) else onNoQrFound()
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to scan image")
        }
    }
}
