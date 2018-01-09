package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2d
import wapuniverse.model.AreaSelection
import wapuniverse.model.AreaSelectionContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.WapObject

class SelectToolContextImpl(
        private val world: WorldImpl
) : SelectToolContext {
    override val selectedObjects: ObservableSet<WapObject> = FXCollections.observableSet()

    override val areaSelection = SimpleObjectProperty<AreaSelection>()

    override fun selectObjectsAt(point: Vec2d) {
        selectedObjects.clear()
        selectedObjects.addAll(world.objectsAt(point))
    }

    override fun selectObjectsByArea(startPoint: Vec2d): AreaSelectionContext {
        return AreaSelectionContextImpl(startPoint, areaSelection, this, world)
    }

    override fun deleteSelectedObjects() {
        world.deleteObjects(selectedObjects)
        selectedObjects.clear()
    }

    fun selectPreselectedObjects() {
        selectedObjects.clear()
        selectedObjects.addAll(areaSelection.value!!.preselectedObjects.value!!)
    }
}