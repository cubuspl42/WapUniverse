package wapuniverse.rez

import javafx.scene.image.Image

class CachingRezImageProvider(
        private val rezImageLoader: RezImageLoader
) : RezImageProvider {
    private val cache = hashMapOf<String, Image?>()

    override suspend fun provideImage(imageRezPath: String): Image? {
        return cache.getOrPut(imageRezPath) { rezImageLoader.loadImage(imageRezPath) }
    }
}
