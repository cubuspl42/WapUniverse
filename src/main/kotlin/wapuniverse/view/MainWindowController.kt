package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import wapuniverse.model.Editor
import wapuniverse.model.MainWindow
import wapuniverse.model.Plane
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.bind
import wapuniverse.view.extensions.map
import wapuniverse.view.util.bind
import wapuniverse.view.util.hideOverflow
import wapuniverse.view.util.pane
import java.net.URL
import java.util.ResourceBundle

class MainWindowController(private val model: MainWindow) : Initializable {
    @FXML
    lateinit var newMenuItem: MenuItem

    @FXML
    lateinit var openMenuItem: MenuItem

    @FXML
    lateinit var saveMenuItem: MenuItem

    @FXML
    lateinit var newButton: Button

    @FXML
    lateinit var openButton: Button

    @FXML
    lateinit var saveButton: Button

    @FXML
    lateinit var editButton: Button

    @FXML
    lateinit var activePlaneComboBox: ComboBox<Plane>

    @FXML
    lateinit var worldViewPane: BorderPane

    private val rezImageProvider: RezImageProvider

    init {
        rezImageProvider = CachingRezImageProvider(model.rezImageLoader)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        bind(newMenuItem, newButton, model.newAction)
        bind(openMenuItem, openButton, model.openAction)
        bind(saveMenuItem, saveButton, model.saveAction)
        bind(editButton, model.editAction)

        activePlaneComboBox.bind<Plane>(
                model.editor.map { it.world.planes },
                model.editor.map { it.activePlane }
        ) { it.name }

        hideOverflow(worldViewPane)

        worldViewPane.centerProperty().bind(model.editor.map { worldView(it) })
    }

    private fun worldView(editor: Editor): Node {
        return pane(editor.planeEditor.map { wapuniverse.view.worldView(it, rezImageProvider) })
    }
}
