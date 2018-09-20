package wapuniverse.model

import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty
import wapuniverse.view.extensions.map

class PlaneEditor(
        val plane: Plane
) {
    val cameraOffset = objectProperty(Vec2i())

    var selectedObjects = objectProperty(listOf<WapObject>())

    val editAction: Action

    init {
        editAction = Action(selectedObjects.map { it.isNotEmpty() }) {

        }
    }

    fun cameraToWorld(point: Vec2i) =
            cameraOffset.value + point

    fun selectObjects(point: Vec2i) {
        unselectAllObjects()
        val objects = plane.findObjectsAt(point)
        objects.forEach { it.iIsSelected.value = true }
        selectedObjects.value = objects
    }

    private fun unselectAllObjects() {
        selectedObjects.value.forEach { it.iIsSelected.value = false }
    }
}
