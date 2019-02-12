package wapuniverse.app.world_preview

import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.CameraMovementDirection
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2d

class ActivePlaneController(
        activePlaneContext: ActivePlaneContext,
        worldPreviewPane: Pane
) : Controller(activePlaneContext, worldPreviewPane) {
    init {
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

        accelerator(KeyCodeCombination(KeyCode.E)) {
            activePlaneContext.editObject()
        }

        subscribe(eventsOf(worldPreviewPane, ScrollEvent.SCROLL)) {
            activePlaneContext.scrollCamera(-Vec2d(it.deltaX, it.deltaY))
        }
    }
}

open class Controller(
        parent: Disposable,
        private val node: Node? = null
) : Disposable(parent) {
    protected fun accelerator(kc: KeyCodeCombination, function: () -> Unit) {
        check(!isDisposed)
        node?.scene?.let { scene ->
            scene.accelerators[kc] = Runnable(function)
            onDisposed.subscribe { scene.accelerators.remove(kc) }
        }
    }
}
