package wapuniverse.app.world_preview

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Pane
import wapuniverse.editor.TileModeContext

class TileModeController(
        context: TileModeContext,
        worldPreviewPane: Pane
) : Controller(context, worldPreviewPane) {
    init {
        accelerator(KeyCodeCombination(KeyCode.SPACE)) { context.insertTile() }
    }
}
