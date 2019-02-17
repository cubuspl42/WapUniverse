package wapuniverse.editor

import io.github.jwap32.v1.Wwd
import io.github.jwap32.v1.WwdHeader
import io.github.jwap32.v1.WwdHeaderFlags
import io.github.jwap32.v1.dumpWwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import org.reactfx.value.Var
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Vec2i
import java.io.OutputStream

class World(
        private val wwd: Wwd,
        private val imageMetadataSupplier: ImageMetadataSupplier,
        private val tileSetMetadataSupplier: TileSetMetadataSupplier
) {
    internal val editor = newSimpleVar<Editor>(null)

    val levelName = Var.newSimpleVar(wwd.header.levelName)

    val author = Var.newSimpleVar(wwd.header.author)

    val birth = Var.newSimpleVar(wwd.header.birth)

    val rezFile = Var.newSimpleVar(wwd.header.rezFile)

    val imageDir = Var.newSimpleVar(wwd.header.imageDir)

    val palRez = Var.newSimpleVar(wwd.header.palRez)

    val startPosition = Var.newSimpleVar(Vec2i(wwd.header.startX, wwd.header.startY))

    val launchApp = Var.newSimpleVar(wwd.header.launchApp)

    private val planesMut = observableArrayList<Plane>()

    val planes = unmodifiableObservableList(planesMut)!!

    private val prefixes = mapOf(
            wwd.header.prefix1 to wwd.header.imageSet1,
            wwd.header.prefix2 to wwd.header.imageSet2,
            wwd.header.prefix3 to wwd.header.imageSet3,
            wwd.header.prefix4 to wwd.header.imageSet4
    )

    init {
        wwd.planes.forEach { wwdPlane ->
            planesMut.add(Plane(this, tileSetMetadataSupplier, wwdPlane))
        }
    }

    internal fun expandImageSetId(imageSetId: String): String? {
        return prefixes.entries.asSequence()
                .mapNotNull { (prefix, expandedPrefix) ->
                    substitutePrefix(imageSetId, prefix, expandedPrefix)
                }.firstOrNull()
    }

    internal fun supplyMetadata(fqImageSetId: String, i: Int): ImageMetadata? {
        return imageMetadataSupplier.supplyMetadata(fqImageSetId, i)
    }

    fun save(stream: OutputStream) {
        val wwd = toWwd()
        dumpWwd(stream, wwd)
    }

    private fun toWwd(): Wwd {
        return wwd.copy( // TODO
                planes = planes.map { it.toWwdPlane() }.toMutableList()
        )
    }
}

private fun substitutePrefix(imageSetId: String, prefix: String, expandedPrefix: String): String? {
    val normalizedExpandedPrefix = expandedPrefix.replace('\\', '_')
    return if (imageSetId.startsWith(prefix)) imageSetId.replaceFirst(prefix, normalizedExpandedPrefix)
    else null
}
