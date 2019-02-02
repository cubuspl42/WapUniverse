package wapuniverse.editor

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.disposeOldValues

class Editor(val world: World) {
    val activePlane = SimpleObjectProperty<Plane>() as Property<Plane>

    val activePlaneContext = activePlane.map(::ActivePlaneContext)
            .apply { disposeOldValues() }
}
