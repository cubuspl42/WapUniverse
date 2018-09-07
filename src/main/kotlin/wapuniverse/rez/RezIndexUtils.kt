package wapuniverse.rez

import kotlinx.coroutines.experimental.runBlocking
import wapuniverse.geom.Vec2i

fun addImageSizes(rezIndex: RezIndex, rezImageLoader: RezImageLoader) =
        rezIndex.copy(imageSets = rezIndex.imageSets.mapValues {
            val imageSet = it.value
            imageSet.copy(images = imageSet.images.mapValues {
                val imageMetadata = it.value
                val image = runBlocking { rezImageLoader.loadImage(imageMetadata.rezPath) }
                val imageSize = image?.let { Vec2i(it.width.toInt(), it.height.toInt()) } ?: Vec2i()
                imageMetadata.copy(size = imageSize)
            })
        })
