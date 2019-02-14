package wapuniverse.app.world_preview

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import org.reactfx.value.Val
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.CameraMovementDirection
import wapuniverse.editor.extensions.map
import wapuniverse.geom.Size2i
import wapuniverse.geom.Vec2d

class ActivePlaneController(
        activePlaneContext: ActivePlaneContext,
        worldPreviewPane: Pane
) : Controller(activePlaneContext, worldPreviewPane) {
    init {
        activePlaneContext.cameraSize.bind(Val.combine(
                worldPreviewPane.widthProperty().map { it.toInt() },
                worldPreviewPane.heightProperty().map { it.toInt() },
                ::Size2i
        ))

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

        subscribe(eventsOf(worldPreviewPane, ScrollEvent.SCROLL)) {
            activePlaneContext.scrollCamera(-Vec2d(it.deltaX, it.deltaY))
        }
    }
}
