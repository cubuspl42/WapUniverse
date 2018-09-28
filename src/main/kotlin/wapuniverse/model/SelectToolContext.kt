package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty
import wapuniverse.util.optionalProperty
import wapuniverse.view.extensions.map

class SelectToolContext(
        private val plane: Plane
) : ToolContext() {
    var selectedObjects = objectProperty(listOf<WapObject>()) // FIXME: var

    val editAction: Action

    val editObjectContext: ObservableValue<EditObjectContext?>

    private val mEditObjectContext = optionalProperty<EditObjectContext?>()

    init {
        editAction = Action(selectedObjects.map { it.isNotEmpty() }) {
            mEditObjectContext.replace(EditObjectContext(selectedObjects.value.first()))
        }

        editObjectContext = mEditObjectContext
    }

    override fun uninit() {
        unselectAllObjects()
    }

    fun selectObjects(point: Vec2i) {
        check(!isDisposed)
        unselectAllObjects()
        val objects = plane.findObjectsAt(point)
        objects.forEach { it.iIsSelected.value = true }
        selectedObjects.value = objects
    }

    private fun unselectAllObjects() {
        selectedObjects.value.forEach { it.iIsSelected.value = false }
        selectedObjects.value = emptyList()
    }
}
