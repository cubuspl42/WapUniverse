package wapuniverse.view

import javafx.scene.Parent
import wapuniverse.model.EditorContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldPresenter(
        private val rezImageProvider: RezImageProvider
) {
    fun presentWorldView(editorContext: EditorContext): Parent {
        return loadFxml(fxmlFilename) { WorldViewController(editorContext, rezImageProvider) }
    }
}
