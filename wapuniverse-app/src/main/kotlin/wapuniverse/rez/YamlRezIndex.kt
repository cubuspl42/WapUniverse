package wapuniverse.rez

import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import wapuniverse.geom.Vec2i
import kotlin.properties.Delegates.vetoable

private const val rezIndexPath = "rezIndex.yaml"

class RezIndex {
    class Image {
        var offset: List<Int> by vetoable(listOf(0, 0)) { _, _, new ->
            if (new.size != 2) throw IllegalArgumentException()
            else true
        }
        var path: String = ""
    }

    class ImageSet {
        var sprites: Map<String, Image> = HashMap() // frame name -> image
        var frames: Map<Int, String> = HashMap() // frame index -> frame name
    }

    var imageSets: Map<String, ImageSet> = HashMap() // fully qualified imageset id -> imageset
}

private val yaml = Yaml(Constructor(RezIndex::class.java).apply {
    addTypeDescription(TypeDescription(RezIndex::Image::class.java))
    addTypeDescription(TypeDescription(RezIndex::ImageSet::class.java))
})

fun loadRezIndex(): RezIndex {
    val classLoader = Thread.currentThread().contextClassLoader
    val root = classLoader.getResourceAsStream(rezIndexPath).use {
        yaml.load(it) as RezIndex
    }
    return root
}
