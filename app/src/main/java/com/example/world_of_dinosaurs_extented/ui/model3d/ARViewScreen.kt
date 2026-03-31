package com.example.world_of_dinosaurs_extented.ui.model3d

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARViewScreen(
    onNavigateBack: () -> Unit,
    viewModel: ARViewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // null = still checking, true = supported, false = not supported
    var arAvailable by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        try {
            var availability = ArCoreApk.getInstance().checkAvailability(context)
            // checkAvailability may return UNKNOWN_CHECKING on first call;
            // poll until result is definitive
            while (availability.isTransient) {
                kotlinx.coroutines.delay(200)
                availability = ArCoreApk.getInstance().checkAvailability(context)
            }
            arAvailable = availability == ArCoreApk.Availability.SUPPORTED_INSTALLED ||
                availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD ||
                availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED
        } catch (_: Exception) {
            arAvailable = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${uiState.dinosaurName} AR") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (uiState.isPlaced) {
                        IconButton(onClick = viewModel::resetPlacement) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reset_placement))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                arAvailable == false -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.ar_not_supported),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
                arAvailable == null || uiState.isLoading || uiState.isDownloading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.isDownloading) stringResource(R.string.downloading_model) else stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                uiState.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
                uiState.modelPath != null -> {
                    ARDinoViewer(
                        modelPath = uiState.modelPath!!,
                        scale = uiState.scale,
                        onPlaced = viewModel::onModelPlaced,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (!uiState.isPlaced) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.tap_to_place),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), MaterialTheme.shapes.small)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.move_phone_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f), MaterialTheme.shapes.small)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ARDinoViewer(
    modelPath: String,
    scale: Float,
    onPlaced: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val childNodes = rememberNodes()
    var loadedInstance by remember { mutableStateOf<ModelInstance?>(null) }
    var currentFrame by remember { mutableStateOf<Frame?>(null) }

    LaunchedEffect(modelPath) {
        try {
            loadedInstance = modelLoader.createModelInstance(modelPath)
        } catch (e: Exception) {
            android.util.Log.e("ARDinoViewer", "Failed to load model: $modelPath", e)
        }
    }

    ARScene(
        modifier = modifier,
        engine = engine,
        modelLoader = modelLoader,
        childNodes = childNodes,
        sessionConfiguration = { session: Session, config: Config ->
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            config.depthMode = Config.DepthMode.DISABLED
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        },
        onSessionUpdated = { _: Session, frame: Frame ->
            currentFrame = frame
        },
        onTouchEvent = { e: MotionEvent, _ ->
            if (e.action == MotionEvent.ACTION_UP && loadedInstance != null) {
                val frame = currentFrame
                if (frame != null) {
                    val arHitResult = frame.hitTest(e.x, e.y)
                        .firstOrNull { hit ->
                            hit.trackable.trackingState == TrackingState.TRACKING
                        }
                    if (arHitResult != null) {
                        loadedInstance?.let { instance ->
                            val anchor = arHitResult.createAnchorOrNull() ?: return@let
                            val anchorNode = AnchorNode(
                                engine = engine,
                                anchor = anchor
                            ).apply {
                                val mNode = ModelNode(
                                    modelInstance = instance,
                                    scaleToUnits = scale
                                ).apply {
                                    position = Position(0f, 0f, 0f)
                                }
                                addChildNode(mNode)
                            }
                            childNodes.add(anchorNode)
                            onPlaced()
                        }
                        true
                    } else false
                } else false
            } else {
                false
            }
        }
    )
}
