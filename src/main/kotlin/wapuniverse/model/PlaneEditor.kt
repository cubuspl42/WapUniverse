package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty

class PlaneEditor(
        val plane: Plane
) {
    val cameraOffset = objectProperty(Vec2i())

    val selectToolContext: ObservableValue<SelectToolContext?>

    private val mSelectToolContext = objectProperty(SelectToolContext(plane))

    init {
        selectToolContext = mSelectToolContext
    }

    fun cameraToWorld(point: Vec2i) =
            cameraOffset.value + point
}
