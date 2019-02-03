package wapuniverse.app.world_preview

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.CameraMovementDirection
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2d

class ActivePlaneController(
        activePlaneContext: ActivePlaneContext,
        worldPreviewPane: Pane
) : Controller(activePlaneContext, worldPreviewPane.scene) {
    init {
        subscribe(dragGesturesOf(worldPreviewPane)) { dragGesture ->
            val position = dragGesture.position.map { it.toVec2i() }
            val areaSelectionContext = activePlaneContext.selectByArea(position)
            dragGesture.onEnded.subscribe { areaSelectionContext.commit() }
        }

        accelerator(KeyCodeCombination(KeyCode.LEFT)) {
            activePlaneContext.moveCamera(CameraMovementDirection.LEFT)
        }

        accelerator(KeyCodeCombination(KeyCode.UP)) {
            activePlaneContext.moveCamera(CameraMovementDirection.UP)
        }

        accelerator(KeyCodeCombination(KeyCode.DOWN)) {
            activePlaneContext.moveCamera(CameraMovementDirection.DOWN)
        }

        accelerator(KeyCodeCombination(KeyCode.RIGHT)) {
            activePlaneContext.moveCamera(CameraMovementDirection.RIGHT)
        }
    }
}

abstract class Controller(
        parent: Disposable,
        private val scene: Scene
) : Disposable(parent) {
    protected fun accelerator(kc: KeyCodeCombination, function: () -> Unit) {
        check(!isDisposed)
        scene.accelerators[kc] = Runnable(function)
        onDisposed.subscribe { scene.accelerators.remove(kc) }
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
