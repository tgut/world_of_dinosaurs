package com.example.world_of_dinosaurs_extented.data.model3d

/**
 * Registry of which dinosaurs have 3D models available.
 * Models can be bundled in assets or downloaded from remote URLs.
 */
object Model3dConfig {

    data class ModelInfo(
        val dinosaurId: String,
        val assetPath: String? = null,      // e.g. "models/trex.glb" (bundled in APK)
        val remoteUrl: String? = null,       // e.g. "https://..." (downloaded on demand)
        val scale: Float = 1.0f
    )

    private val models = mapOf(
        // Bundled models (in APK assets)
        "trex" to ModelInfo(
            dinosaurId = "trex",
            assetPath = "models/tyrannosaurus_rex.glb",
            scale = 0.5f
        ),
        "triceratops" to ModelInfo(
            dinosaurId = "triceratops",
            assetPath = "models/triceratops.glb",
            scale = 0.5f
        ),
        "stegosaurus" to ModelInfo(
            dinosaurId = "stegosaurus",
            assetPath = "models/stegosaurus.glb",
            scale = 0.5f
        ),
        // Remote models can be added here when hosted GLB URLs are available:
        // "velociraptor" to ModelInfo(dinosaurId = "velociraptor", remoteUrl = "https://...", scale = 0.8f),
        // "brachiosaurus" to ModelInfo(dinosaurId = "brachiosaurus", remoteUrl = "https://...", scale = 0.3f),
        // "spinosaurus" to ModelInfo(dinosaurId = "spinosaurus", remoteUrl = "https://...", scale = 0.5f),
        // "pteranodon" to ModelInfo(dinosaurId = "pteranodon", remoteUrl = "https://...", scale = 0.6f),
        // "ankylosaurus" to ModelInfo(dinosaurId = "ankylosaurus", remoteUrl = "https://...", scale = 0.5f)
    )

    fun hasModel(dinosaurId: String): Boolean =
        models.containsKey(dinosaurId)

    fun getModelInfo(dinosaurId: String): ModelInfo? =
        models[dinosaurId]

    fun getAllModelIds(): Set<String> = models.keys
}
