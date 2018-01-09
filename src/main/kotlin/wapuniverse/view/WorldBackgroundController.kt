package wapuniverse.view

import javafx.scene.shape.Rectangle
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.view.ext.position

class WorldBackgroundController(
        backgroundRect: Rectangle,
        editorContext: EditorContext
) {
    init {
        backgroundRect.setOnMousePressed { ev ->
            (editorContext.activeToolContext.value as? SelectToolContext)?.let { selectToolContext ->
                val areaSelectionContext = selectToolContext.selectObjectsByArea(ev.position)
                AreaSelectionController(backgroundRect, areaSelectionContext)
            }
        }
    }
}
