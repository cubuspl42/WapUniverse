package wapuniverse.app.world_preview

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2d

class ActivePlaneController(
        activePlaneContext: ActivePlaneContext,
        worldPreviewPane: Pane
) : Disposable(activePlaneContext) {
    init {
        subscribe(dragGesturesOf(worldPreviewPane)) { dragGesture ->
            val position = dragGesture.position.map { it.toVec2i() }
            val areaSelectionContext = activePlaneContext.selectByArea(position)
            dragGesture.onEnded.subscribe { areaSelectionContext.commit() }
        }
    }
}

private class DragGesture(event: MouseEvent, node: Node) : Disposable() {
    val position = eventsOf(node, MouseEvent.MOUSE_DRAGGED)
            .map { Vec2d(it.x, it.y) }
            .toBinding(Vec2d(event.x, event.y)) as ObservableValue<Vec2d>

    val onEnded = eventsOf(node, MouseEvent.MOUSE_RELEASED).map { Unit }!!

    init {
        subscribe(onEnded) {
            dispose()
        }
    }
}

private fun dragGesturesOf(node: Node) =
        eventsOf(node, MouseEvent.MOUSE_PRESSED).map {
            DragGesture(it, node)
        }
