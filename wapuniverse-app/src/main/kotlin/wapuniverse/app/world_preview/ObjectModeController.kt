package wapuniverse.app.world_preview

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Pane
import wapuniverse.editor.ObjectModeContext

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