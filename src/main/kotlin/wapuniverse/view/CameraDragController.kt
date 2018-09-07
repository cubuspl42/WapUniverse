package wapuniverse.view

import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.reactfx.EventStreams
import org.reactfx.Subscription
import wapuniverse.geom.Vec2d
import wapuniverse.model.PlaneEditor

val CAMERA_DRAG_MOUSE_BUTTON = MouseButton.SECONDARY

class CameraDragController(
        private val model: PlaneEditor,
        node: Node,
        e1: MouseEvent
) {
    private val cameraStartPosition = model.cameraOffset.value!!

    private val startPosition = Vec2d(e1.x, e1.y)

    private val s1: Subscription

    private var s2: Subscription? = null

    init {
        s1 = EventStreams.eventsOf(node, MouseEvent.MOUSE_DRAGGED).subscribe { e ->
            if (e.button != CAMERA_DRAG_MOUSE_BUTTON) return@subscribe
            val newPosition = Vec2d(e.x, e.y)
            val delta = newPosition - startPosition
            model.cameraOffset.value = cameraStartPosition - delta.toVec2i()
        }
        s2 = EventStreams.eventsOf(node, MouseEvent.MOUSE_RELEASED).subscribe { e ->
            if (e.button != CAMERA_DRAG_MOUSE_BUTTON) return@subscribe
            s1.unsubscribe()
            s2!!.unsubscribe()
        }
    }
}
