package wapuniverse.editor

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import wapuniverse.editor.extensions.map

class Editor(val world: World) {
    val activePlane = SimpleObjectProperty<Plane>() as Property<Plane>

    val activePlaneContext = activePlane.map(::ActivePlaneContext)
}
