package wapuniverse.rez

import javafx.scene.image.Image

interface RezImageLoader {
    fun loadImage(imageRezPath: String): Image?
}
