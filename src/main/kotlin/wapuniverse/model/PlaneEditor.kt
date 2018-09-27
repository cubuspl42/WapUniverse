package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.transform
import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty

class PlaneEditor(
        editor: Editor,
        val plane: Plane
) {
    val cameraOffset = objectProperty(Vec2i())

    val toolContext: ObservableValue<ToolContext>

    val selectToolContext: ObservableValue<SelectToolContext?>

    init {
        toolContext = editor.tool.transform { SelectToolContext(plane) }
        selectToolContext = toolContext.map { it as? SelectToolContext }
    }

    fun cameraToWorld(point: Vec2i) =
            cameraOffset.value + point
}
