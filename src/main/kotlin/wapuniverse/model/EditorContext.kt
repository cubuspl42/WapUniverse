package wapuniverse.model

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.monadic.MonadicObservableValue
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.model.impl.PlaneContext
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.map

interface EditorContext {
    val world: World

    val activeTool: Property<Tool>

    val activePlane: Property<out Plane>

    val activePlaneContext: ObservableValue<out PlaneContext?>
}

//fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)

val PlaneContext.selectToolContext: MonadicObservableValue<SelectToolContext>
    get() = activeToolContext.map { it as? SelectToolContext }

val PlaneContext.moveToolContext: MonadicObservableValue<MoveToolContext>
    get() = activeToolContext.map { it as? MoveToolContext }
