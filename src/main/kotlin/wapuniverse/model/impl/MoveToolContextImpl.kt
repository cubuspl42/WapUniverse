package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import wapuniverse.geom.Vec2d
import wapuniverse.model.MoveToolContext
import wapuniverse.model.ObjectsDrag

class MoveToolContextImpl(
        private val plane: PlaneImpl
) : MoveToolContext {
    override val objectsDrag = SimpleObjectProperty<ObjectsDrag>()

    override fun dragSelectedObjects(dragOrigin: Vec2d) = ObjectsDragContextImpl(dragOrigin, objectsDrag, plane)
}
