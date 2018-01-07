package wapuniverse.view

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
        val worldGroup = worldPresenter.presentWorldGroup(editorContext.world)

        pane.center = presentWorldView(worldGroup)

        imageSetText.textProperty().bind(editorContext.world.objects.first().imageSet)
    }
}
