package wapuniverse.view

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.model.MainContext
import wapuniverse.view.ext.map
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/EditorContextView.fxml"

class MainWindowPresenter(
        private val editorContextPresenter: EditorContextPresenter
) {
    fun showMainWindow(
            mainContext: MainContext
    ) {
        val stage = Stage()
        val root = BorderPane().apply {
            centerProperty().bind(mainContext.editorContext.map {
                editorContextPresenter.presentEditorContext(it)
            })
        }
        stage.scene = Scene(root)
        stage.show()
    }
}

class EditorContextPresenter(
        private val worldPresenter: WorldPresenter
) {
    fun presentEditorContext(editorContext: EditorContext): Parent {
        return loadFxml(fxmlFilename) {
            EditorContextController(worldPresenter, editorContext)
        }
    }
}