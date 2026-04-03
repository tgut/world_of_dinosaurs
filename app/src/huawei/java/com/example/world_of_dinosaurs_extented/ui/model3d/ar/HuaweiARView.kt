package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARWorldTrackingConfig
import com.huawei.hiar.ARConfigBase
import com.huawei.hiar.exceptions.ARSessionPausedException
import com.huawei.hiar.ARTrackable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * huawei flavor: AR scene viewer using Huawei AR Engine.
 *
 * Renders the AR session on a GLSurfaceView. Model loading and anchor placement
 * use AR Engine APIs (ARSession, ARFrame, ARHitResult).
 */
@Composable
fun ARDinoViewer(
    modelPath: String,
    scale: Float,
    onPlaced: () -> Unit,
    modifier: Modifier = Modifier
) {
    var arSession by remember { mutableStateOf<ARSession?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            arSession?.stop()
            arSession = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val session = ARSession(context)
            val config = ARWorldTrackingConfig(session).apply {
                setPlaneFindingMode(ARConfigBase.PlaneFindingMode.ENABLE)
            }
            session.configure(config)
            arSession = session

            val surfaceView = GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(object : GLSurfaceView.Renderer {
                    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                        session.resume()
                    }

                    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                        session.setDisplayGeometry(0, width, height)
                    }

                    override fun onDrawFrame(gl: GL10?) {
                        try {
                            session.update()
                            // Render background camera feed and tracked planes here.
                            // Full rendering pipeline requires a CameraTextureRenderer —
                            // this stub calls update() to keep the AR session alive.
                        } catch (_: ARSessionPausedException) {
                            // Session not yet resumed; safe to ignore
                        }
                    }
                })
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        val sess = arSession ?: return@setOnTouchListener false
                        try {
                            val frame = sess.update()
                            val hitResults = frame.hitTest(event)
                            val hit = hitResults.firstOrNull { result ->
                                result.trackable.trackingState == ARTrackable.TrackingState.TRACKING
                            }
                            if (hit != null) {
                                hit.createAnchor()
                                android.util.Log.d("HuaweiAR", "Placed model $modelPath scale $scale")
                                onPlaced()
                                true
                            } else false
                        } catch (e: Exception) {
                            android.util.Log.e("HuaweiAR", "HitTest failed", e)
                            false
                        }
                    } else false
                }
            }
            surfaceView
        }
    )
}
