package wapuniverse.model

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.rez.RezIndex

interface EditorContext {
    val world: World

    val hoverPositionProperty: Property<Vec2d>

    val activeTool: Property<Tool>

    val activeToolContext: ObservableValue<out ToolContext>
}

fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)
