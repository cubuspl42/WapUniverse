package wapuniverse.editor

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import org.reactfx.value.Val
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.disposeOldValues

class Editor(val world: World) {
    private val modeVar = newSimpleVar(Mode.OBJECT)

    val mode = modeVar as Val<Mode>

    fun switchMode() {
        if (mode.value == Mode.OBJECT) modeVar.value = Mode.TILE
        else modeVar.value = Mode.OBJECT
    }

    val activePlane = SimpleObjectProperty<Plane>() as Property<Plane>

    val activePlaneContext = activePlane.map { ActivePlaneContext(this, it) }
            .apply { disposeOldValues() }

    init {
        world.editor.value = this
    }
}
