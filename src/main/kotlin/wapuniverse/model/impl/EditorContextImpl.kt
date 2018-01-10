package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2d
import wapuniverse.model.EditorContext
import wapuniverse.model.Tool
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.map

class EditorContextImpl(
        rezIndex: RezIndex
) : EditorContext {
    override val world = WorldImpl(this, rezIndex)

    override val hoverPositionProperty = SimpleObjectProperty<Vec2d>()

    val hoveredObjects = monadic(hoverPositionProperty)
            .map { world.objectsAt(it) }
            .orElse(emptySet())!!

    override val activeTool = SimpleObjectProperty<Tool>(Tool.SELECT)

    override val activeToolContext = activeTool.map {
        it!!
        when (it) {
            Tool.SELECT -> SelectToolContextImpl(world)
            Tool.MOVE -> MoveToolContextImpl(world)
        }
    }

    init {
        world.init()
    }
}
