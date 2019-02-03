package wapuniverse.app.world_preview

import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Pane
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.CameraMovementDirection
import wapuniverse.editor.util.Disposable

class ActivePlaneController(
        activePlaneContext: ActivePlaneContext,
        worldPreviewPane: Pane
) : Controller(activePlaneContext, { worldPreviewPane.scene }) {
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
    }
}

abstract class Controller(
        parent: Disposable,
        private val getScene: () -> Scene
) : Disposable(parent) {
    protected fun accelerator(kc: KeyCodeCombination, function: () -> Unit) {
        val scene = getScene()
        check(!isDisposed)
        scene.accelerators[kc] = Runnable(function)
        onDisposed.subscribe { scene.accelerators.remove(kc) }
    }
}
