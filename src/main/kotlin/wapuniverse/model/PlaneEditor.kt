package wapuniverse.model

import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty

class PlaneEditor(
        val plane: Plane
) {
    val cameraOffset = objectProperty(Vec2i())

    var selectedObjects = listOf<WapObject>()

    fun cameraToWorld(point: Vec2i) =
            cameraOffset.value + point

    fun selectObjects(point: Vec2i) {
        unselectAllObjects()
        val objects = plane.findObjectsAt(point)
        objects.forEach { it.iIsSelected.value = true }
        selectedObjects = objects
    }

    private fun unselectAllObjects() {
        selectedObjects.forEach { it.iIsSelected.value = false }
    }
}
