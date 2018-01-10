package wapuniverse.view

import javafx.scene.Node
import wapuniverse.model.MoveToolContext
import wapuniverse.view.ext.position

class MoveToolObjectController(
        private val node: Node,
        private val moveToolContext: MoveToolContext
) : Controller by DraggableNodeController(
        node,
        onMouseDragged = { event ->
            val objectsDragContext = moveToolContext.dragSelectedObjects(event.position)
            ObjectsDragController(node, objectsDragContext)
        }
)
