package wapuniverse.view

import javafx.scene.Node
import wapuniverse.model.ObjectsDragContext
import wapuniverse.view.ext.position

class ObjectsDragController(
        private val node: Node,
        private val objectsDragContext: ObjectsDragContext
) : Controller by DragGestureController(
        node,
        onMouseDragged = { event ->
            objectsDragContext.setDestination(event.position)
        },
        onMouseReleased = { event ->
            objectsDragContext.commit()
        }
)
