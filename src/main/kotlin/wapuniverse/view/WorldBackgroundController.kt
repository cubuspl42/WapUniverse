package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.scene.shape.Rectangle
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.view.ext.position

class WorldBackgroundController(
        backgroundRect: Rectangle,
        selectToolContext: ObservableValue<out SelectToolContext?>
) {
    init {
        backgroundRect.setOnMousePressed { ev ->
            selectToolContext.value?.let { selectToolContext ->
                val areaSelectionContext = selectToolContext.selectObjectsByArea(ev.position)
                AreaSelectionController(backgroundRect, areaSelectionContext)
            }
        }
    }
}
