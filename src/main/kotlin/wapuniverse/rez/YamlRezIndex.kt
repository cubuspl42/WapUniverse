package wapuniverse.rez

import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import wapuniverse.geom.Vec2i
import java.io.InputStream
import kotlin.properties.Delegates.vetoable

/**
 * Classes passed to snakeyaml cannot be private, but can be enclosed in a private class.
 */
private class Y {
    class Image {
        var offset: List<Int> by vetoable(listOf(0, 0), { _, _, new ->
            if (new.size != 2) throw IllegalArgumentException()
            else true
        })
        var path: String = ""

        fun toRezImageMetadata() = RezImageMetadata(path, Vec2i(offset[0], offset[1]), Vec2i())
    }

    class ImageSet {
        var sprites: Map<String, Image> = HashMap() // frame name -> image
        var frames: Map<Int, String> = HashMap() // frame index -> frame name

        fun toRezImageSet() = RezImageSet(
                sprites.mapValues { it.value.toRezImageMetadata() },
                frames
        )
    }

    class Root {
        var imageSets: Map<String, ImageSet> = HashMap() // fully qualified imageset id -> imageset

        fun toRezIndex() = RezIndex(imageSets.mapValues { it.value.toRezImageSet() })
    }
}

private val yaml = Yaml(Constructor(Y.Root::class.java).apply {
    addTypeDescription(TypeDescription(Y.Image::class.java))
    addTypeDescription(TypeDescription(Y.ImageSet::class.java))
})

fun loadYamlRezIndex(yamlStream: InputStream): RezIndex {
    val root = yamlStream.use {
        yaml.load(it) as Y.Root
    }
    return root.toRezIndex()
}
