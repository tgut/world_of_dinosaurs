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
        ),
        // Additional popular dinosaurs (remote URLs for dynamic loading)
        "iguanodon" to ModelInfo(
            dinosaurId = "iguanodon",
            remoteUrl = "https://models.sketchfab.com/iguanodon.glb",
            scale = 0.45f
        ),
        "gallimimus" to ModelInfo(
            dinosaurId = "gallimimus",
            remoteUrl = "https://models.sketchfab.com/gallimimus.glb",
            scale = 0.6f
        ),
        "compsognathus" to ModelInfo(
            dinosaurId = "compsognathus",
            remoteUrl = "https://models.sketchfab.com/compsognathus.glb",
            scale = 0.9f
        ),
        "deinonychus" to ModelInfo(
            dinosaurId = "deinonychus",
            remoteUrl = "https://models.sketchfab.com/deinonychus.glb",
            scale = 0.65f
        ),
        "oviraptor" to ModelInfo(
            dinosaurId = "oviraptor",
            remoteUrl = "https://models.sketchfab.com/oviraptor.glb",
            scale = 0.7f
        ),
        "troodon" to ModelInfo(
            dinosaurId = "troodon",
            remoteUrl = "https://models.sketchfab.com/troodon.glb",
            scale = 0.75f
        ),
        "therizinosaurus" to ModelInfo(
            dinosaurId = "therizinosaurus",
            remoteUrl = "https://models.sketchfab.com/therizinosaurus.glb",
            scale = 0.35f
        ),
        "diplodocus" to ModelInfo(
            dinosaurId = "diplodocus",
            remoteUrl = "https://models.sketchfab.com/diplodocus.glb",
            scale = 0.35f
        ),
        "apatosaurus" to ModelInfo(
            dinosaurId = "apatosaurus",
            remoteUrl = "https://models.sketchfab.com/apatosaurus.glb",
            scale = 0.35f
        ),
        "archaeopteryx" to ModelInfo(
            dinosaurId = "archaeopteryx",
            remoteUrl = "https://models.sketchfab.com/archaeopteryx.glb",
            scale = 0.8f
        ),
        "eoraptor" to ModelInfo(
            dinosaurId = "eoraptor",
            remoteUrl = "https://models.sketchfab.com/eoraptor.glb",
            scale = 0.75f
        ),
        "heterodontosaurus" to ModelInfo(
            dinosaurId = "heterodontosaurus",
            remoteUrl = "https://models.sketchfab.com/heterodontosaurus.glb",
            scale = 0.8f
        ),
        "hypsilophodon" to ModelInfo(
            dinosaurId = "hypsilophodon",
            remoteUrl = "https://models.sketchfab.com/hypsilophodon.glb",
            scale = 0.75f
        ),
        "iguanodon" to ModelInfo(
            dinosaurId = "iguanodon",
            remoteUrl = "https://models.sketchfab.com/iguanodon.glb",
            scale = 0.45f
        ),
        "corythosaurus" to ModelInfo(
            dinosaurId = "corythosaurus",
            remoteUrl = "https://models.sketchfab.com/corythosaurus.glb",
            scale = 0.45f
        ),
        "edmontosaurus" to ModelInfo(
            dinosaurId = "edmontosaurus",
            remoteUrl = "https://models.sketchfab.com/edmontosaurus.glb",
            scale = 0.4f
        ),
        "ceratosaurus" to ModelInfo(
            dinosaurId = "ceratosaurus",
            remoteUrl = "https://models.sketchfab.com/ceratosaurus.glb",
            scale = 0.5f
        ),
        "allosaurus" to ModelInfo(
            dinosaurId = "allosaurus",
            remoteUrl = "https://models.sketchfab.com/allosaurus.glb",
            scale = 0.5f
        ),
        "kentrosaurus" to ModelInfo(
            dinosaurId = "kentrosaurus",
            remoteUrl = "https://models.sketchfab.com/kentrosaurus.glb",
            scale = 0.55f
        ),
        "euoplocephalus" to ModelInfo(
            dinosaurId = "euoplocephalus",
            remoteUrl = "https://models.sketchfab.com/euoplocephalus.glb",
            scale = 0.5f
        ),
        "nodosaurus" to ModelInfo(
            dinosaurId = "nodosaurus",
            remoteUrl = "https://models.sketchfab.com/nodosaurus.glb",
            scale = 0.5f
        ),
        "sauroposeidon" to ModelInfo(
            dinosaurId = "sauroposeidon",
            remoteUrl = "https://models.sketchfab.com/sauroposeidon.glb",
            scale = 0.25f
        ),
        "alamosaurus" to ModelInfo(
            dinosaurId = "alamosaurus",
            remoteUrl = "https://models.sketchfab.com/alamosaurus.glb",
            scale = 0.3f
        ),
        "plateosaurus" to ModelInfo(
            dinosaurId = "plateosaurus",
            remoteUrl = "https://models.sketchfab.com/plateosaurus.glb",
            scale = 0.45f
        ),
        "thecodontosaurus" to ModelInfo(
            dinosaurId = "thecodontosaurus",
            remoteUrl = "https://models.sketchfab.com/thecodontosaurus.glb",
            scale = 0.7f
        ),
        "massospondylus" to ModelInfo(
            dinosaurId = "massospondylus",
            remoteUrl = "https://models.sketchfab.com/massospondylus.glb",
            scale = 0.6f
        ),
        "riojasaurus" to ModelInfo(
            dinosaurId = "riojasaurus",
            remoteUrl = "https://models.sketchfab.com/riojasaurus.glb",
            scale = 0.4f
        )
    )

    fun hasModel(dinosaurId: String): Boolean =
        models.containsKey(dinosaurId)

    fun getModelInfo(dinosaurId: String): ModelInfo? =
        models[dinosaurId]

    fun getAllModelIds(): Set<String> = models.keys
}
