package com.example.world_of_dinosaurs_extented.ui.model3d

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Model3DScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAR: ((String) -> Unit)? = null,
    viewModel: Model3DViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.dinosaurName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (onNavigateToAR != null && uiState.modelPath != null) {
                        IconButton(onClick = { onNavigateToAR(uiState.dinosaurId) }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = stringResource(R.string.view_in_ar))
                        }
                    }
                }
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
                uiState.isLoading || uiState.isDownloading -> {
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
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                uiState.modelPath != null -> {
                    Dino3DViewer(
                        modelPath = uiState.modelPath!!,
                        scale = uiState.scale,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = stringResource(R.string.model_gesture_hint),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Dino3DViewer(
    modelPath: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val modelNodes = rememberNodes()

    LaunchedEffect(modelPath) {
        try {
            val modelInstance = modelLoader.createModelInstance(modelPath)
            val modelNode = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits = scale
            ).apply {
                position = Position(0f, 0f, -2f)
            }
            modelNodes.clear()
            modelNodes.add(modelNode)
        } catch (_: Exception) {}
    }

    Scene(
        modifier = modifier,
        engine = engine,
        modelLoader = modelLoader,
        childNodes = modelNodes
    )
}
