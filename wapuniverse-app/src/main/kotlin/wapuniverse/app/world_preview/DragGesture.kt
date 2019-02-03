package wapuniverse.app.world_preview

import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import org.reactfx.EventStreams
import org.reactfx.EventStreams.eventsOf
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2d

class DragGesture(event: MouseEvent, node: Node) : Disposable() {
    val position = EventStreams.eventsOf(node, MouseEvent.MOUSE_DRAGGED)
            .map { Vec2d(it.x, it.y) }
            .toBinding(Vec2d(event.x, event.y)) as ObservableValue<Vec2d>

    val onEnded = EventStreams.eventsOf(node, MouseEvent.MOUSE_RELEASED).map { Unit }!!

    init {
        subscribe(onEnded) {
            dispose()
        }
    }
}

fun dragGesturesOf(node: Node) =
        eventsOf(node, MouseEvent.MOUSE_PRESSED).map {
            DragGesture(it, node)
        }!!
