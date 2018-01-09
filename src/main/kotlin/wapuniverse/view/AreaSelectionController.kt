package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import wapuniverse.model.AreaSelectionContext
import wapuniverse.view.ext.position

class AreaSelectionController(
        private val surface: Node,
        private val areaSelectionContext: AreaSelectionContext
) {
    private val onMouseDragged = EventHandler<MouseEvent> { ev ->
        areaSelectionContext.setEndPoint(ev.position)
    }

    private val onMouseReleased = EventHandler<MouseEvent> {
        areaSelectionContext.commit()
        uninit()
    }

    init {
        surface.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged)
        surface.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased)
    }

    private fun uninit() {
        surface.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged)
        surface.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased)
    }
}
