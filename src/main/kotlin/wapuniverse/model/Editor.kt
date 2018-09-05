package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import io.github.jwap32.v1.Wwd
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import wapuniverse.util.booleanProperty
import wapuniverse.util.objectProperty
import wapuniverse.view.extensions.map

class Editor(
       wwd: Wwd
) {
    val world = World(wwd)

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

    private fun createPlaneEditor(): ObservableValue<PlaneEditor?> {
        return activePlane.map { PlaneEditor(it) }
    }
}
