package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableSet
import javafx.collections.ObservableSet
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2d
import wapuniverse.model.EditorContext
import wapuniverse.model.WapObject
import wapuniverse.rez.RezIndex

class EditorContextImpl(
        rezIndex: RezIndex
) : EditorContext {
    override val hoverPositionProperty = SimpleObjectProperty<Vec2d>()

    val hoveredObjects = monadic(hoverPositionProperty)
            .map { world.objectsAt(it) }
            .orElse(emptySet())!!

    override val selectedObjects: ObservableSet<WapObject> = observableSet()

    override val world = WorldImpl(this, rezIndex)

    override fun selectObjectsAt(point: Vec2d) {
        selectedObjects.clear()
        selectedObjects.addAll(world.objectsAt(point))
    }
}
