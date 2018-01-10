package wapuniverse.view

import javafx.scene.Node
import wapuniverse.model.AreaSelectionContext
import wapuniverse.view.ext.position

class AreaSelectionController(
        private val surface: Node,
        private val areaSelectionContext: AreaSelectionContext
) : Controller by DragGestureController(
        surface,
        onMouseDragged = { event ->
            areaSelectionContext.setEndPoint(event.position)
        },
        onMouseReleased = { event ->
            areaSelectionContext.commit()
        }
)
