package com.example.world_of_dinosaurs_extented.ui.map

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurMapMarker
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GlobeWebView(
    markers: List<DinosaurMapMarker>,
    language: String,
    focusDinosaurId: String? = null,
    focusLat: Double?,
    focusLng: Double?,
    autoRotateTimeoutMs: Long = 10000L,
    onMarkerClick: (DinosaurMapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    val markerMap = remember(markers) { markers.associateBy { it.dinosaurId } }
    val markersJson = remember(markers, language) { buildMarkersJson(markers, language) }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onMarkerClick(dinosaurId: String) {
                        val marker = markerMap[dinosaurId]
                        if (marker != null) {
                            post { onMarkerClick(marker) }
                        }
                    }

                    @JavascriptInterface
                    fun onGlobeReady() {
                        // Globe is fully initialized, no action needed
                        // since JS already flushes pending commands
                    }
                }, "AndroidBridge")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // JS functions queue calls until globe.gl finishes loading,
                        // so it's safe to call them here even before CDN loads
                        val escaped = markersJson.replace("\\", "\\\\").replace("'", "\\'")
                        if (focusDinosaurId != null) {
                            evaluateJavascript("setFocusId('$focusDinosaurId')", null)
                        }
                        evaluateJavascript("loadMarkers('$escaped')", null)
                        evaluateJavascript("setLanguage('$language')", null)
                        evaluateJavascript("setAutoRotateTimeout($autoRotateTimeoutMs)", null)
                        if (focusLat != null && focusLng != null) {
                            evaluateJavascript("focusOnPoint($focusLat, $focusLng)", null)
                        }
                    }
                }

                loadUrl("file:///android_asset/globe.html")
            }
        },
        update = { webView ->
            val escaped = markersJson.replace("\\", "\\\\").replace("'", "\\'")
            if (focusDinosaurId != null) {
                webView.evaluateJavascript("setFocusId('$focusDinosaurId')", null)
            }
            webView.evaluateJavascript("loadMarkers('$escaped')", null)
            webView.evaluateJavascript("setLanguage('$language')", null)
            webView.evaluateJavascript("setAutoRotateTimeout($autoRotateTimeoutMs)", null)
        },
        modifier = modifier
    )
}

private fun buildMarkersJson(markers: List<DinosaurMapMarker>, language: String): String {
    val array = JSONArray()
    markers.forEach { m ->
        val obj = JSONObject().apply {
            put("id", m.dinosaurId)
            put("lat", m.lat)
            put("lng", m.lng)
            put("name", m.getLocalizedName(language))
            put("location", m.discoveryLocation)
            put("color", eraToHexColor(m.era))
            put("size", 0.3)
        }
        array.put(obj)
    }
    return array.toString()
}

private fun eraToHexColor(era: DinosaurEra): String = when (era) {
    DinosaurEra.TRIASSIC -> "#8D6E63"
    DinosaurEra.JURASSIC -> "#4CAF50"
    DinosaurEra.CRETACEOUS -> "#2196F3"
}
