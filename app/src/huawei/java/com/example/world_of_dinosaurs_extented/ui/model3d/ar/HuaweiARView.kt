package com.example.world_of_dinosaurs_extented.ui.model3d.ar

import android.opengl.GLES11Ext
import android.opengl.GLES20
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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * huawei flavor: AR scene viewer using Huawei AR Engine.
 *
 * Renders the live camera feed as an OES texture on a full-screen quad,
 * then performs hit-testing against tracked planes on tap.
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

            val renderer = HuaweiARRenderer(session, modelPath, scale, onPlaced)

            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                setOnTouchListener { _, event ->
                    // Copy the event so it's safe to use off the main thread
                    if (event.action == MotionEvent.ACTION_UP) {
                        renderer.enqueueTap(MotionEvent.obtain(event))
                        true
                    } else false
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Renderer
// ---------------------------------------------------------------------------

private class HuaweiARRenderer(
    private val session: ARSession,
    private val modelPath: String,
    private val scale: Float,
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
            val tap = pendingTap
            if (tap != null) {
                pendingTap = null
                val hits = frame.hitTest(tap)
                tap.recycle()
                val hit = hits.firstOrNull { r ->
                    r.trackable.trackingState == ARTrackable.TrackingState.TRACKING
                }
                if (hit != null) {
                    hit.createAnchor()
                    android.util.Log.d("HuaweiAR", "Placed $modelPath scale=$scale")
                    onPlaced()
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
