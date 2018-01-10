package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2d

interface MoveToolContext : ToolContext {
    val objectsDrag: ObservableValue<ObjectsDrag>

    fun dragSelectedObjects(dragOrigin: Vec2d): ObjectsDragContext
}

interface ObjectsDrag {
    val origin: Vec2d
    val delta: ObservableValue<Vec2d>
}
