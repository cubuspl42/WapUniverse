package wapuniverse.view

import javafx.scene.image.ImageView
import wapuniverse.model.MoveToolContext
import wapuniverse.view.ext.position

class MoveToolObjectController(
        private val imageView: ImageView,
        private val moveToolContext: MoveToolContext
) : Controller by DraggableNodeController(
        imageView,
        onMouseDragged = { event ->
            val objectsDragContext = moveToolContext.dragSelectedObjects(event.position)
            ObjectsDragController(imageView, objectsDragContext)
        }
)
