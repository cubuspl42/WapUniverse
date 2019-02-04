package wapuniverse.app.world_preview

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import org.reactfx.EventStream
import org.reactfx.EventStreams.eventsOf
import org.reactfx.value.Val
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2d

class DragGesture(event: MouseEvent, node: Node) : Disposable() {
    val position = eventsOf(node, MouseEvent.MOUSE_DRAGGED)
            .map { Vec2d(it.x, it.y) }
            .toVal(Vec2d(event.x, event.y))

    val onEnded = eventsOf(node, MouseEvent.MOUSE_RELEASED).map { Unit }!!

    init {
        subscribe(onEnded) {
            dispose()
        }
    }
}

private fun <T> EventStream<T>.toVal(initialValue: T) =
        Val.wrap(toBinding(initialValue))!!

fun dragGesturesOf(node: Node) =
        eventsOf(node, MouseEvent.MOUSE_PRESSED).map {
            DragGesture(it, node)
        }!!
