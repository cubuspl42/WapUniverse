package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import wapuniverse.model.AreaSelectionContext
import wapuniverse.view.ext.position

class AreaSelectionController(
        private val backgroundRect: Rectangle,
        private val areaSelectionContext: AreaSelectionContext
) {
    private val onMouseDragged = EventHandler<MouseEvent> { ev ->
        areaSelectionContext.setEndPoint(ev.position)
    }

    private  val onMouseReleased = EventHandler<MouseEvent> {
        areaSelectionContext.commit()
        uninit()
    }

    init {
        backgroundRect.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged)
        backgroundRect.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased)
    }

    private fun uninit() {
        backgroundRect.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged)
        backgroundRect.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased)
    }
}
