package wapuniverse.rez

import javafx.scene.image.Image
import wapuniverse.geom.Vec2i

private const val prefix = "CLAW"

fun buildRezImageCache(rezIndex: RezIndex) =
        RezImageCache(rezIndex.imageSets.mapValues { (_, imageSet) ->
            RezImageSet(
                    imageSet.sprites.mapValues { (_, metadata) ->
                        loadRezImage(metadata)
                    },
                    imageSet.frames
            )
        })

fun loadRezImage(image: RezIndex.Image): RezImage {
    val realPath = realizePath(image.path)
    val classLoader = Thread.currentThread().contextClassLoader
    val stream = classLoader.getResourceAsStream(realPath)
    val img = stream?.let { Image(it) }
    return RezImage(img, Vec2i(image.offset[0], image.offset[1]))
}

private fun realizePath(imageRezPath: String): String =
        "$prefix/${imageRezPath.replace(".PID", ".png")}"
