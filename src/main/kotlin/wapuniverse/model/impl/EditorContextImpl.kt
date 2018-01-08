package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2d
import wapuniverse.model.EditorContext
import wapuniverse.rez.RezIndex

class EditorContextImpl(
        rezIndex: RezIndex
) : EditorContext {
    override val hoverPositionProperty = SimpleObjectProperty<Vec2d>()

    val hoveredObjects = monadic(hoverPositionProperty)
            .map { world.objectsAt(it) }
            .orElse(emptySet())!!

    override val world = WorldImpl(this, rezIndex)
}
