package wapuniverse.rez

import javafx.scene.image.Image
import wapuniverse.geom.Vec2i

private const val prefix = "CLAW"

fun buildRezImageCache(rezIndex: RezIndex) =
        RezImageCache(rezIndex.imageSets.mapValues { (_, imageSet) ->
            RezImageSet(
                    imageSet.sprites.mapValues { (_, metadata) ->
                        RezImage(
                                loadRezImage(metadata.path), Vec2i(metadata.offset[0], metadata.offset[1])
                        )
                    },
                    imageSet.frames
            )
        })

fun loadRezImage(imageRezPath: String): Image? {
    val realPath = realizePath(imageRezPath)
    val classLoader = Thread.currentThread().contextClassLoader
    val stream = classLoader.getResourceAsStream(realPath)
    return stream?.let { Image(it) }
}

private fun realizePath(imageRezPath: String): String =
        "$prefix/${imageRezPath.replace(".PID", ".png")}"
