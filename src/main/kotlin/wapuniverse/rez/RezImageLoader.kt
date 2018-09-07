package wapuniverse.rez

import javafx.scene.image.Image

interface RezImageLoader {
    suspend fun loadImage(imageRezPath: String): Image?
}