package wapuniverse.model

import io.github.jwap32.v1.Wwd
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import wapuniverse.rez.RezIndex
import wapuniverse.util.booleanProperty
import wapuniverse.util.objectProperty
import wapuniverse.view.extensions.map

class Editor(
        wwd: Wwd,
        private val rezIndex: RezIndex
) {
    val world = World(wwd, this)

    val saved: ObservableBooleanValue

    fun isSaved(): Boolean = saved.value

    val activePlane = objectProperty<Plane>(world.planes.getOrNull(0))

    val planeEditor: ObservableValue<PlaneEditor?> = createPlaneEditor()

    private val mSaved = booleanProperty(false)

    init {
        saved = mSaved
    }

    fun save() {
        check(!isSaved())
    }

    internal fun findObjectImageMetadata(shortImageSetId: String, i: Int) =
            world.prefixMap.entries
                    .asSequence()
                    .map { (shortPrefix, imageSetPrefix) ->
                        val longImageSetId = shortImageSetId
                                .replaceFirst(shortPrefix, imageSetPrefix)
                                .replace("\\", "_")
                        rezIndex.findImageMetadata(longImageSetId, i)
                    }
                    .filterNotNull()
                    .firstOrNull()

    private fun createPlaneEditor(): ObservableValue<PlaneEditor?> {
        return activePlane.map { PlaneEditor(it) }
    }
}
