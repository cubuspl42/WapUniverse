package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent

class DragGestureController(
        private val node: Node,
        private val onMouseDragged: (MouseEvent) -> Unit,
        private val onMouseReleased: (MouseEvent) -> Unit
) : Controller {
    private val onMouseDraggedHandler = EventHandler<MouseEvent> {
        onMouseDragged(it)
    }

    private val onMouseReleasedHandler = EventHandler<MouseEvent> {
        onMouseReleased(it)
        uninit()
    }

    init {
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedHandler)
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedHandler)
    }

    override fun uninit() {
        node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedHandler)
        node.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedHandler)
    }
}
