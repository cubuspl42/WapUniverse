package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.transform
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.Disposable
import wapuniverse.util.objectProperty

class PlaneEditor(
        editor: Editor,
        val plane: Plane
): Disposable() {
    val cameraOffset = objectProperty(Vec2i())

    val toolContext: ObservableValue<ToolContext>

    val selectToolContext: ObservableValue<SelectToolContext?>

    val pencilToolContext: ObservableValue<PencilToolContext?>

    init {
        toolContext = editor.tool.transform { tool ->
            when (tool) {
                Tool.SELECT -> SelectToolContext(plane)
                Tool.PENCIL -> PencilToolContext(plane)
            }
        }
        selectToolContext = toolContext.map { it as? SelectToolContext }

        pencilToolContext = toolContext.map { it as? PencilToolContext }
    }

    fun cameraToWorld(point: Vec2i) =
            cameraOffset.value + point
}
