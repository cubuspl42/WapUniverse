package wapuniverse.model

import javafx.beans.property.Property
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.rez.RezIndex

interface EditorContext {
    val world: World

    val hoverPositionProperty: Property<Vec2d>

    val selectedObjects: ObservableSet<WapObject>

    fun selectObjectsAt(point: Vec2d)
}

fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)
