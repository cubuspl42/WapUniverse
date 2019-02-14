package wapuniverse.app.world_preview

import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import org.reactfx.value.Val
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.CameraMovementDirection
import wapuniverse.editor.ObjectModeContext
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Size2i
import wapuniverse.geom.Vec2d

class ObjectModeController(
        objectModeContext: ObjectModeContext,
        worldPreviewPane: Pane
) : Controller(objectModeContext, worldPreviewPane) {
    init {
        accelerator(KeyCodeCombination(KeyCode.E)) {
            objectModeContext.editObject()
        }
    }
}