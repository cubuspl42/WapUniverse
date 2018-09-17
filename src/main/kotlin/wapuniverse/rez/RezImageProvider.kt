package wapuniverse.rez

import javafx.scene.image.Image

interface RezImageProvider {
    suspend fun provideImage(imageRezPath: String): Image?
}
