package wapuniverse.model

import io.github.jwap32.v1.Wwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.UnmodifiableCollection
import wapuniverse.rez.RezIndex

class World(
        private val wwd: Wwd,
        private val rezIndex: RezIndex
) {
    val imageDir = wwd.header.imageDir

    val prefixMap = mapOf(
            wwd.header.prefix1 to wwd.header.imageSet1,
            wwd.header.prefix2 to wwd.header.imageSet2,
            wwd.header.prefix3 to wwd.header.imageSet3,
            wwd.header.prefix4 to wwd.header.imageSet4
    )

    @UnmodifiableCollection
    val planes: ObservableList<Plane>

    private val mPlanes: ObservableList<Plane> = createPlanes(wwd)

    private fun createPlanes(wwd: Wwd) =
            observableArrayList<Plane>(wwd.planes.map { Plane(this, it, rezIndex) })

    init {
        planes = unmodifiableObservableList(mPlanes)!!
    }

    fun toWwd(): Wwd {
        val wwd = wwd.clone()
        wwd.planes = planes.map { it.toWwdPlane() }.toMutableList()
        return wwd
    }

    internal fun findObjectImageMetadata(shortImageSetId: String, i: Int) =
            prefixMap.entries.asSequence()
                    .mapNotNull { (shortPrefix, imageSetPrefix) ->
                        val longImageSetId = shortImageSetId
                                .replaceFirst(shortPrefix, imageSetPrefix)
                                .replace("\\", "_")
                        rezIndex.findImageMetadata(longImageSetId, i)
                    }
                    .firstOrNull()
}
