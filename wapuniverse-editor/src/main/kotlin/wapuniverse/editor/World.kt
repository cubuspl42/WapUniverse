package wapuniverse.editor

import io.github.jwap32.v1.Wwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList

class World(
        wwd: Wwd,
        private val imageMetadataSupplier: ImageMetadataSupplier
) {
    internal val imageDir = wwd.header.imageDir

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
            planesMut.add(Plane(this, wwdPlane))
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
}

private fun substitutePrefix(imageSetId: String, prefix: String, expandedPrefix: String): String? {
    val normalizedExpandedPrefix = expandedPrefix.replace('\\', '_')
    return if (imageSetId.startsWith(prefix)) imageSetId.replaceFirst(prefix, normalizedExpandedPrefix)
    else null
}
