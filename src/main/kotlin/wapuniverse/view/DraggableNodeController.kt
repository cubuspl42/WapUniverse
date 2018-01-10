package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent

class DraggableNodeController(
        private val node: Node,
        private val onMouseDragged: (MouseEvent) -> Unit
) : Controller {
    private val onMousePressedHandler = EventHandler<MouseEvent> { onMouseDragged(it) }

    init {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedHandler)
    }

    override fun uninit() {
        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedHandler)
    }
}