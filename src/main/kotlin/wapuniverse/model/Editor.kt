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
        rezIndex: RezIndex
) {
    val world = World(wwd, rezIndex)

    val tool = objectProperty(Tool.SELECT)

    val saved: ObservableBooleanValue

    fun isSaved(): Boolean = saved.value

    val activePlane = objectProperty(world.planes.getOrNull(0))

    val planeEditor: ObservableValue<PlaneEditor?> = createPlaneEditor()

    private val mSaved = booleanProperty(false)

    init {
        saved = mSaved
    }

    fun save() {
        check(!isSaved())
    }

    private fun createPlaneEditor(): ObservableValue<PlaneEditor?> {
        return activePlane.map { PlaneEditor(this, it) }
    }
}
