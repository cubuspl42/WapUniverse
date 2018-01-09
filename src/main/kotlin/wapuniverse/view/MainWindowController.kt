package wapuniverse.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import wapuniverse.model.EditorContext
import java.net.URL
import java.util.ResourceBundle

class MainWindowController(
        private val worldPresenter: WorldPresenter,
        private val editorContext: EditorContext
) : Initializable {
    @FXML
    lateinit var pane: BorderPane

    @FXML
    lateinit var imageSetText: Text

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        pane.center = worldPresenter.presentWorldView(editorContext)

        imageSetText.textProperty().bind(editorContext.world.objects.first().imageSet)
    }

    fun onDelete(actionEvent: ActionEvent) {
        editorContext.deleteSelectedObjects()
    }
}
