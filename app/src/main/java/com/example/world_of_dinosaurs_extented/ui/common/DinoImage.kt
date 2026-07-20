package com.example.world_of_dinosaurs_extented.ui.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/**
 * Load a dinosaur image.
 *
 * Strategy (in order):
 * 1. Try from local assets by dino ID (all common extensions) — works offline, fastest
 * 2. Fall back to Coil for the original imageUrl (remote or local asset scheme)
 *    which handles the case where a local file exists but BitmapFactory couldn't decode it
 */
@Composable
fun DinoImage(
    imageUrl: String?,
    dinosaurId: String? = null,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    // Step 1: try loading from local assets by dinosaur ID
    val id = dinosaurId ?: imageUrl?.substringAfterLast("/")?.substringBeforeLast(".")
    var localBitmap by remember(id) { mutableStateOf<Bitmap?>(null) }
    var localTried by remember(id) { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id != null && !localTried) {
            localTried = true
            val exts = listOf("jpg", "png", "JPG", "PNG", "gif")
            for (ext in exts) {
                val path = "images/$id.$ext"
                try {
                    context.assets.open(path).use { input ->
                        val bm = BitmapFactory.decodeStream(input)
                        if (bm != null) {
                            Log.d("DinoIMG", "Loaded asset: $path ${bm.width}x${bm.height}")
                            localBitmap = bm
                            return@LaunchedEffect
                        }
                    }
                } catch (_: Exception) { }
            }
            Log.d("DinoIMG", "No asset found for id=$id")
        }
    }

    if (localBitmap != null) {
        Image(
            bitmap = localBitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
        return
    }

    // Step 2: fall back to Coil with the original URL
    val imgUrl = imageUrl
    if (!imgUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imgUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            loading = { placeholder() },
            error = { placeholder() }
        )
    } else {
        placeholder()
    }
}
