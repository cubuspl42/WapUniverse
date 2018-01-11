package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import wapuniverse.geom.Vec2d
import wapuniverse.model.AreaSelection
import wapuniverse.model.AreaSelectionContext
import wapuniverse.model.SelectToolContext

class SelectToolContextImpl(
        private val world: WorldImpl
) : SelectToolContext {

    override val areaSelection = SimpleObjectProperty<AreaSelectionImpl>()

    override fun selectObjectsAt(point: Vec2d) {
        world.selectObjects(world.objectsAt(point))
    }

    override fun selectObjectsByArea(startPoint: Vec2d): AreaSelectionContext {
        return AreaSelectionContextImpl(startPoint, areaSelection, this, world)
    }

    override fun deleteSelectedObjects() {
        world.deleteObjects(world.selectedObjects)
    }

    fun selectPreselectedObjects() {
        world.selectObjects(areaSelection.value!!.preselectedObjects.value!!)
    }
}
