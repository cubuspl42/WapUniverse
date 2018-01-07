package wapuniverse.view

import javafx.application.Application.launch
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color.RED
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import wapuniverse.model.EditorContext
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.view.util.loadFxml
import wapuniverse.view.util.observableValue
import java.net.URL
import java.util.ResourceBundle

private val fxmlFilename = "/fxml/MainWindow.fxml"

class MainWindowController(
        private val rezImageProvider: CachingRezImageProvider,
        private val editorContext: EditorContext
) : Initializable {
    @FXML
    lateinit var pane: BorderPane

    @FXML
    lateinit var imageSetText: Text

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val root = Group()

        val image = observableValue {
            rezImageProvider.provideImage(
                    "LEVEL1_IMAGES_OFFICER", -1
            )?.image
        }

        val imageView = ImageView()
        imageView.imageProperty().bind(image)

        root.children.add(imageView)

        pane.center = presentWorldView(root)

        imageSetText.textProperty().bind(editorContext.world.singleObject.imageSet)
    }
}

fun showMainWindow(
        rezImageProvider: CachingRezImageProvider,
        editorContext: EditorContext
) {
    val stage = Stage()
    val root = loadFxml(fxmlFilename) { MainWindowController(rezImageProvider, editorContext) }
    stage.scene = Scene(root)
    stage.show()
}
