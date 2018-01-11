package wapuniverse.model

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2d

interface SelectToolContext : ToolContext {
    val areaSelection: ObservableValue<out AreaSelection>

    fun selectObjectsAt(point: Vec2d)

    fun selectObjectsByArea(startPoint: Vec2d): AreaSelectionContext

    fun deleteSelectedObjects()
}