package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARWorldTrackingConfig
import com.huawei.hiar.ARConfigBase
import com.huawei.hiar.exceptions.ARSessionPausedException
import com.huawei.hiar.ARTrackable
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.model.ModelInstance
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * huawei flavor: AR scene viewer using Huawei AR Engine.
 *
 * Layer 1 (bottom): GLSurfaceView renders the live camera feed as an OES
 *   texture on a full-screen quad and performs hit-testing against tracked
 *   planes on tap.
 * Layer 2 (top):  SceneView (Filament) renders the GLB model as a Compose
 *   overlay with a transparent background, shown once placement succeeds.
 *   Using SceneView's non-AR mode avoids the GMS/ARCore dependency while
 *   still giving us PBR rendering of the same GLB assets.
 */
@Composable
fun ARDinoViewer(
    modelPath: String,
    scale: Float,
    onPlaced: () -> Unit,
    modifier: Modifier = Modifier
) {
    var arSession by remember { mutableStateOf<ARSession?>(null) }
    // Set to true when the user successfully taps a tracked plane
    var modelVisible by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            arSession?.stop()
            arSession = null
        }
    }

    Box(modifier = modifier) {
        // -----------------------------------------------------------------
        // Layer 1 — camera background + AR plane hit-test
        // -----------------------------------------------------------------
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val session = ARSession(context)
                val config = ARWorldTrackingConfig(session).apply {
                    setPlaneFindingMode(ARConfigBase.PlaneFindingMode.ENABLE)
                }
                session.configure(config)
                arSession = session

                val placedCallback = {
                    modelVisible = true
                    onPlaced()
                }
                val renderer = HuaweiARRenderer(session, placedCallback)

                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(renderer)
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    setOnTouchListener { _, event ->
                        // Always return true so ACTION_DOWN is consumed;
                        // otherwise Android drops the gesture and ACTION_UP
                        // is never delivered to this listener.
                        if (event.action == MotionEvent.ACTION_UP) {
                            renderer.enqueueTap(MotionEvent.obtain(event))
                        }
                        true
                    }
                }
            }
        )

        // -----------------------------------------------------------------
        // Layer 2 — Filament SceneView model overlay (transparent bg)
        // -----------------------------------------------------------------
        if (modelVisible) {
            val engine          = rememberEngine()
            val modelLoader     = rememberModelLoader(engine)
            val environmentLoader = rememberEnvironmentLoader(engine)
            val environment     = rememberEnvironment(environmentLoader)
            val mainLightNode   = rememberMainLightNode(engine) { intensity = 100_000f }
            val modelNodes      = rememberNodes()

            LaunchedEffect(modelPath) {
                try {
                    val instance: ModelInstance = modelLoader.createModelInstance(modelPath)
                    val node = ModelNode(
                        modelInstance = instance,
                        scaleToUnits = scale * 1.5f
                    ).apply {
                        position = Position(0f, 0f, -1.5f)   // 1.5 m in front of camera
                    }
                    modelNodes.clear()
                    modelNodes.add(node)
                } catch (e: Exception) {
                    android.util.Log.e("HuaweiAR", "Failed to load model: $modelPath", e)
                }
            }

            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                environment = environment,
                mainLightNode = mainLightNode,
                childNodes = modelNodes,
                // Transparent so the camera feed shows through underneath
                isOpaque = false
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Renderer — camera background + plane hit-testing
// ---------------------------------------------------------------------------

private class HuaweiARRenderer(
    private val session: ARSession,
    private val onPlaced: () -> Unit
) : GLSurfaceView.Renderer {

    // Camera OES texture
    private var cameraTextureId = -1

    // Background quad shader program handles
    private var bgProgram = 0
    private var bgPositionHandle = 0
    private var bgTexCoordHandle = 0
    private var bgTextureHandle = 0

    // Full-screen quad: two triangles as a TRIANGLE_STRIP
    //   (-1,-1)  (1,-1)
    //   (-1, 1)  (1, 1)
    private val quadVerts = floatArrayOf(
        -1f, -1f,
         1f, -1f,
        -1f,  1f,
         1f,  1f
    )

    // UV coords rotated for portrait (camera sensor is landscape)
    //   bottom-left → (1,1), bottom-right → (1,0)
    //   top-left    → (0,1), top-right    → (0,0)
    private val quadUvs = floatArrayOf(
        1f, 1f,
        1f, 0f,
        0f, 1f,
        0f, 0f
    )

    private val vertBuf = ByteBuffer.allocateDirect(quadVerts.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
        .also { it.put(quadVerts); it.position(0) }

    private val uvBuf = ByteBuffer.allocateDirect(quadUvs.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
        .also { it.put(quadUvs); it.position(0) }

    // Thread-safe pending tap: stored from touch thread, consumed in GL thread
    @Volatile private var pendingTap: MotionEvent? = null
    // Guard so onPlaced() fires only once
    @Volatile private var placed = false

    fun enqueueTap(event: MotionEvent) {
        pendingTap?.recycle()
        pendingTap = event
    }

    // -------------------------------------------------------------------
    // GLSurfaceView.Renderer
    // -------------------------------------------------------------------

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // 1. Create the OES texture that AR Engine will write camera frames into
        val tex = IntArray(1)
        GLES20.glGenTextures(1, tex, 0)
        cameraTextureId = tex[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // 2. Register the texture with AR Engine BEFORE resume()
        session.setCameraTextureName(cameraTextureId)
        session.resume()

        // 3. Compile background shader
        bgProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        bgPositionHandle = GLES20.glGetAttribLocation(bgProgram, "a_Position")
        bgTexCoordHandle = GLES20.glGetAttribLocation(bgProgram, "a_TexCoord")
        bgTextureHandle  = GLES20.glGetUniformLocation(bgProgram, "u_Texture")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        // rotation=0 means portrait; AR Engine will apply the correct crop/rotation
        session.setDisplayGeometry(0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        try {
            // Update the AR session and get the latest frame
            val frame = session.update()

            // --- Draw camera background ---
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
            GLES20.glDepthMask(false)

            GLES20.glUseProgram(bgProgram)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId)
            GLES20.glUniform1i(bgTextureHandle, 0)

            vertBuf.position(0)
            GLES20.glEnableVertexAttribArray(bgPositionHandle)
            GLES20.glVertexAttribPointer(bgPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vertBuf)

            uvBuf.position(0)
            GLES20.glEnableVertexAttribArray(bgTexCoordHandle)
            GLES20.glVertexAttribPointer(bgTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuf)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            GLES20.glDisableVertexAttribArray(bgPositionHandle)
            GLES20.glDisableVertexAttribArray(bgTexCoordHandle)
            GLES20.glDepthMask(true)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

            // --- Process tap (hit-test against tracked planes) ---
            if (!placed) {
                val tap = pendingTap
                if (tap != null) {
                    pendingTap = null
                    val hits = frame.hitTest(tap)
                    tap.recycle()
                    val hit = hits.firstOrNull { r ->
                        r.trackable.trackingState == ARTrackable.TrackingState.TRACKING
                    }
                    if (hit != null) {
                        placed = true
                        hit.createAnchor()
                        android.util.Log.d("HuaweiAR", "Plane hit — showing model overlay")
                        // Switch to main thread for Compose state update
                        android.os.Handler(android.os.Looper.getMainLooper()).post { onPlaced() }
                    }
                }
            }

        } catch (_: ARSessionPausedException) {
            // Session not yet resumed; safe to ignore
        } catch (e: Exception) {
            android.util.Log.e("HuaweiAR", "onDrawFrame error", e)
        }
    }

    // -------------------------------------------------------------------
    // GL helpers
    // -------------------------------------------------------------------

    private fun createProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER, vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        return GLES20.glCreateProgram().also { prog ->
            GLES20.glAttachShader(prog, vert)
            GLES20.glAttachShader(prog, frag)
            GLES20.glLinkProgram(prog)
        }
    }

    private fun compileShader(type: Int, src: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, src)
            GLES20.glCompileShader(shader)
        }
    }

    // -------------------------------------------------------------------
    // Shader sources
    // -------------------------------------------------------------------

    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            void main() {
                gl_Position = a_Position;
                v_TexCoord  = a_TexCoord;
            }
        """.trimIndent()

        // samplerExternalOES is required for the camera OES texture
        private val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES u_Texture;
            varying vec2 v_TexCoord;
            void main() {
                gl_FragColor = texture2D(u_Texture, v_TexCoord);
            }
        """.trimIndent()
    }
}
