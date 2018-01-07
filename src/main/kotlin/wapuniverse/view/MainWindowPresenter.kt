package wapuniverse.view

import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/MainWindow.fxml"

class MainWindowPresenter(
        private val worldPresenter: WorldPresenter
) {
    fun showMainWindow(
            editorContext: EditorContext
    ) {
        val stage = Stage()
        val root = loadFxml(fxmlFilename) { MainWindowController(worldPresenter, editorContext) }
        stage.scene = Scene(root)
        stage.show()
    }
}
