package com.example.world_of_dinosaurs_extented.ui.common

import android.graphics.BitmapFactory
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

/**
 * Load a dinosaur image from either:
 * 1. Local assets (file:///android_asset/...) via AssetManager — works on all devices
 * 2. Remote URL via Coil
 * Falls back to [placeholder] on failure.
 */
@Composable
fun DinoImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    if (imageUrl.isNullOrBlank()) {
        placeholder()
        return
    }

    val context = LocalContext.current

    if (imageUrl.startsWith("file:///android_asset/")) {
        val assetPath = imageUrl.removePrefix("file:///android_asset/")
        val bitmap = remember(assetPath) {
            try {
                val inputStream = context.assets.open(assetPath)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) { null }
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
            placeholder()
        }
    } else {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            loading = { placeholder() },
            error = { placeholder() }
        )
    }
}
