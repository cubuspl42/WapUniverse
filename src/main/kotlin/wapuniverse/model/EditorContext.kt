package wapuniverse.model

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableSet
import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.rez.RezIndex

interface EditorContext {
    val world: World

    val hoverPositionProperty: Property<Vec2d>

    val selectedObjects: ObservableSet<WapObject>

    val areaSelection: ObservableValue<AreaSelection>

    fun selectObjectsAt(point: Vec2d)

    fun selectObjectsByArea(startPoint: Vec2d): AreaSelectionContext
}

interface AreaSelection {
    val boundingBox: ObservableValue<BoundingBox>
    val preselectedObjects: ObservableValue<Set<WapObject>>
}

interface AreaSelectionContext {
    fun setEndPoint(endPoint: Vec2d)
    fun commit()
}

fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)
