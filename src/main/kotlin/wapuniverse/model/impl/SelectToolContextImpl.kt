package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import wapuniverse.geom.Vec2d
import wapuniverse.model.AreaSelectionContext
import wapuniverse.model.SelectToolContext

class SelectToolContextImpl(
        private val plane: PlaneImpl
) : SelectToolContext {

    override val areaSelection = SimpleObjectProperty<AreaSelectionImpl>()

    override fun selectObjectsAt(point: Vec2d) {
        plane.selectObjects(plane.objectsAt(point))
    }

    override fun selectObjectsByArea(startPoint: Vec2d): AreaSelectionContext {
        return AreaSelectionContextImpl(startPoint, areaSelection, this, plane)
    }

    override fun deleteSelectedObjects() {
        plane.deleteObjects(plane.selectedObjects)
    }

    fun selectPreselectedObjects() {
        plane.selectObjects(areaSelection.value!!.preselectedObjects.value!!)
    }
}
