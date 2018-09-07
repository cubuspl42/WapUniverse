package wapuniverse.model

import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty

class PlaneEditor(
        val plane: Plane
) {
    val cameraOffset = objectProperty(Vec2i())
}
