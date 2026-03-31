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
        "velociraptor" to ModelInfo(
            dinosaurId = "velociraptor",
            assetPath = "models/velociraptor.glb",
            scale = 0.7f
        ),
        "brachiosaurus" to ModelInfo(
            dinosaurId = "brachiosaurus",
            assetPath = "models/brachiosaurus.glb",
            scale = 0.3f
        ),
        "spinosaurus" to ModelInfo(
            dinosaurId = "spinosaurus",
            assetPath = "models/spinosaurus.glb",
            scale = 0.5f
        ),
        "pteranodon" to ModelInfo(
            dinosaurId = "pteranodon",
            assetPath = "models/pteranodon.glb",
            scale = 0.6f
        ),
        "ankylosaurus" to ModelInfo(
            dinosaurId = "ankylosaurus",
            assetPath = "models/ankylosaurus.glb",
            scale = 0.5f
        ),
        "parasaurolophus" to ModelInfo(
            dinosaurId = "parasaurolophus",
            assetPath = "models/parasaurolophus.glb",
            scale = 0.5f
        ),
        "carnotaurus" to ModelInfo(
            dinosaurId = "carnotaurus",
            assetPath = "models/carnotaurus.glb",
            scale = 0.5f
        )
    )

    fun hasModel(dinosaurId: String): Boolean =
        models.containsKey(dinosaurId)

    fun getModelInfo(dinosaurId: String): ModelInfo? =
        models[dinosaurId]

    fun getAllModelIds(): Set<String> = models.keys
}
