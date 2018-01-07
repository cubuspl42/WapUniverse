package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.text.Text
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.view.util.loadFxml
import java.net.URL
import java.util.ResourceBundle

private val fxmlFilename = "/fxml/MainWindow.fxml"

class MainWindowController(
        private val editorContext: EditorContext
) : Initializable {
    @FXML
    lateinit var imageSetText: Text

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        imageSetText.textProperty().bind(editorContext.world.singleObject.imageSet)
    }
}

fun showMainWindow(
        editorContext: EditorContext
) {
    val stage = Stage()
    val root = loadFxml(fxmlFilename) { MainWindowController(editorContext) }
    stage.scene = Scene(root)
    stage.show()
}
