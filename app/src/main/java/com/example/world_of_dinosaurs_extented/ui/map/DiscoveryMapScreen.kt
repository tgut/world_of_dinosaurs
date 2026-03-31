package com.example.world_of_dinosaurs_extented.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurMapMarker
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryMapScreen(
    onNavigateBack: () -> Unit,
    onDinosaurClick: (String) -> Unit,
    viewModel: DiscoveryMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Configure OSMDroid once
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.discovery_map)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleMapStyle) {
                        Icon(Icons.Default.Layers, contentDescription = stringResource(R.string.map_style))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                if (uiState.mapStyle == MapStyle.GLOBE) {
                    GlobeWebView(
                        markers = uiState.markers,
                        language = uiState.language,
                        focusDinosaurId = uiState.focusDinosaurId,
                        focusLat = uiState.focusLat,
                        focusLng = uiState.focusLng,
                        autoRotateTimeoutMs = uiState.globeRotateTimeoutMs,
                        onMarkerClick = { marker ->
                            onDinosaurClick(marker.dinosaurId)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    OsmMapView(
                        markers = uiState.markers,
                        language = uiState.language,
                        mapStyle = uiState.mapStyle,
                        focusDinosaurId = uiState.focusDinosaurId,
                        focusLat = uiState.focusLat,
                        focusLng = uiState.focusLng,
                        focusZoom = uiState.focusZoom,
                        onMarkerClick = { marker -> viewModel.onMarkerSelected(marker) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Map style label
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = when (uiState.mapStyle) {
                            MapStyle.FLAT -> stringResource(R.string.map_flat)
                            MapStyle.SATELLITE -> stringResource(R.string.map_satellite)
                            MapStyle.GLOBE -> stringResource(R.string.map_globe)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Bottom info card when a marker is selected (only for 2D maps)
                if (uiState.mapStyle != MapStyle.GLOBE) {
                    uiState.selectedMarker?.let { selected ->
                        MarkerInfoCard(
                            marker = selected,
                            language = uiState.language,
                            onViewDetail = { onDinosaurClick(selected.dinosaurId) },
                            onDismiss = { viewModel.onMarkerSelected(null) },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OsmMapView(
    markers: List<DinosaurMapMarker>,
    language: String,
    mapStyle: MapStyle,
    focusDinosaurId: String?,
    focusLat: Double?,
    focusLng: Double?,
    focusZoom: Double?,
    onMarkerClick: (DinosaurMapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                if (focusLat != null && focusLng != null) {
                    controller.setZoom(focusZoom ?: 6.0)
                    controller.setCenter(GeoPoint(focusLat, focusLng))
                } else {
                    controller.setZoom(3.0)
                    controller.setCenter(GeoPoint(20.0, 0.0))
                }
            }
        },
        update = { mapView ->
            // Update tile source based on style
            val newTileSource = when (mapStyle) {
                MapStyle.FLAT -> TileSourceFactory.MAPNIK
                MapStyle.SATELLITE -> TileSourceFactory.OpenTopo
                MapStyle.GLOBE -> TileSourceFactory.MAPNIK // not used for globe
            }
            if (mapView.tileProvider.tileSource != newTileSource) {
                mapView.setTileSource(newTileSource)
            }

            mapView.overlays.clear()
            markers.forEach { dinoMarker ->
                val isFocused = dinoMarker.dinosaurId == focusDinosaurId
                val osmMarker = Marker(mapView).apply {
                    position = GeoPoint(dinoMarker.lat, dinoMarker.lng)
                    title = dinoMarker.getLocalizedName(language)
                    snippet = dinoMarker.discoveryLocation
                    if (isFocused) {
                        // Focused marker: larger with name label, default pin anchor
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = createFocusMarkerDrawable(mapView.context, eraToColor(dinoMarker.era))
                        showInfoWindow()
                    } else {
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        icon = createMarkerDrawable(eraToColor(dinoMarker.era))
                    }
                    setOnMarkerClickListener { _, _ ->
                        onMarkerClick(dinoMarker)
                        true
                    }
                }
                mapView.overlays.add(osmMarker)
            }
            mapView.invalidate()
        },
        modifier = modifier
    )
}

@Composable
private fun MarkerInfoCard(
    marker: DinosaurMapMarker,
    language: String,
    onViewDetail: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = marker.getLocalizedName(language),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = marker.discoveryLocation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onViewDetail) {
                    Text(stringResource(R.string.view_detail))
                }
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

private fun createMarkerDrawable(color: Int): android.graphics.drawable.Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setSize(36, 36)
        setColor(color)
        setStroke(2, 0xFFFFFFFF.toInt())
    }
}

private fun createFocusMarkerDrawable(context: Context, color: Int): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val w = (48 * density).toInt()
    val h = (48 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw vertical line (pin stem)
    paint.color = color
    paint.strokeWidth = 3 * density
    paint.style = Paint.Style.STROKE
    canvas.drawLine(w / 2f, h * 0.3f, w / 2f, h.toFloat(), paint)

    // Draw filled circle (pin head)
    paint.style = Paint.Style.FILL
    canvas.drawCircle(w / 2f, h * 0.25f, 10 * density, paint)

    // Draw white border on circle
    paint.style = Paint.Style.STROKE
    paint.color = 0xFFFFFFFF.toInt()
    paint.strokeWidth = 2 * density
    canvas.drawCircle(w / 2f, h * 0.25f, 10 * density, paint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

private fun eraToColor(era: DinosaurEra): Int = when (era) {
    DinosaurEra.TRIASSIC -> 0xFF8D6E63.toInt()
    DinosaurEra.JURASSIC -> 0xFF2E7D32.toInt()
    DinosaurEra.CRETACEOUS -> 0xFF1565C0.toInt()
}
