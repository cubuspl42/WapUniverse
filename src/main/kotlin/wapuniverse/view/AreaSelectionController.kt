package wapuniverse.view

import javafx.scene.shape.Rectangle
import wapuniverse.model.AreaSelectionContext
import wapuniverse.view.ext.position

class AreaSelectionController(
        backgroundRect: Rectangle,
        areaSelectionContext: AreaSelectionContext
) {
    init {
        backgroundRect.setOnMouseDragged { ev ->
            areaSelectionContext.setEndPoint(ev.position)
        }

        backgroundRect.setOnMouseReleased {
            areaSelectionContext.commit()
        }
    }
}
