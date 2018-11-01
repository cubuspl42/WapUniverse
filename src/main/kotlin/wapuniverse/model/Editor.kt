package wapuniverse.model

import io.github.jwap32.v1.Wwd
import io.github.jwap32.v1.dumpWwd
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import wapuniverse.rez.RezIndex
import wapuniverse.util.booleanProperty
import wapuniverse.util.objectProperty
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.transform
import java.nio.file.Files
import java.nio.file.Files.newOutputStream
import java.nio.file.Path

class Editor(
        wwd: Wwd,
        rezIndex: RezIndex,
        val path: Path
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
        newOutputStream(path).use {
            dumpWwd(it, world.toWwd())
        }
    }

    private fun createPlaneEditor(): ObservableValue<PlaneEditor?> {
        return activePlane.transform { PlaneEditor(this, it) }
    }
}
