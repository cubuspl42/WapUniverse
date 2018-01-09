package wapuniverse.view

import javafx.scene.shape.Rectangle
import wapuniverse.model.EditorContext
import wapuniverse.view.ext.position

class WorldBackgroundController(
        backgroundRect: Rectangle,
        editorContext: EditorContext
) {
    init {
        backgroundRect.setOnMousePressed { ev ->
            val areaSelectionContext = editorContext.selectObjectsByArea(ev.position)
            AreaSelectionController(backgroundRect, areaSelectionContext)
        }
    }
}
