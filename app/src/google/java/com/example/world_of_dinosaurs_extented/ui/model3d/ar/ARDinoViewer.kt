package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.view.MotionEvent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

/**
 * google flavor: AR scene viewer using ARCore + SceneView.
 */
@Composable
fun ARDinoViewer(
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
                            val anchorNode = AnchorNode(engine = engine, anchor = anchor).apply {
                                addChildNode(
                                    ModelNode(modelInstance = instance, scaleToUnits = scale).apply {
                                        position = Position(0f, 0f, 0f)
                                    }
                                )
                            }
                            childNodes.add(anchorNode)
                            onPlaced()
                        }
                        true
                    } else false
                } else false
            } else false
        }
    )
}
