package wapuniverse.model

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.monadic.MonadicObservableValue
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.map

interface EditorContext {
    val world: World

    val hoverPositionProperty: Property<Vec2d>

    val activeTool: Property<Tool>

    val activeToolContext: ObservableValue<out ToolContext>
}

fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)

val EditorContext.selectToolContext: MonadicObservableValue<SelectToolContext>
    get() = activeToolContext.map { it as? SelectToolContext }

val EditorContext.moveToolContext: MonadicObservableValue<MoveToolContext>
    get() = activeToolContext.map { it as? MoveToolContext }
