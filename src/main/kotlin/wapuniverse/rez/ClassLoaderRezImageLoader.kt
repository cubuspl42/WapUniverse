package wapuniverse.rez

import javafx.scene.image.Image

class ClassLoaderRezImageLoader(private val prefix: String) : RezImageLoader {
    private fun realizePath(imageRezPath: String): String =
            "$prefix/${imageRezPath.replace(".PID", ".png")}"

    override fun loadImage(imageRezPath: String): Image? {
        val realPath = realizePath(imageRezPath)
        val classLoader = Thread.currentThread().contextClassLoader
        val stream = classLoader.getResourceAsStream(realPath)
        return stream?.let { Image(it) }
    }
}
