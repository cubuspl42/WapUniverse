package wapuniverse.rez

import javafx.scene.image.Image

interface RezImageProvider {
    fun provideImage(imageRezPath: String): Image?
}
