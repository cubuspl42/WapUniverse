package wapuniverse.view

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.MenuItem
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import wapuniverse.model.Editor
import wapuniverse.model.MainWindow
import wapuniverse.model.Plane
import wapuniverse.model.Tool
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.bind
import wapuniverse.view.extensions.flatMap
import wapuniverse.view.extensions.forEach
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.subscribe
import wapuniverse.view.util.bind
import wapuniverse.view.util.hideOverflow
import wapuniverse.view.util.pane
import java.net.URL
import java.util.ResourceBundle

class MainWindowController(
        private val mainWindow: MainWindow
) : Initializable {
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

    lateinit var toolToggleGroup: ToggleGroup

    @FXML
    lateinit var selectToolButton: ToggleButton

    @FXML
    lateinit var pencilToolButton: ToggleButton

    @FXML
    lateinit var activePlaneComboBox: ComboBox<Plane>

    @FXML
    lateinit var worldViewPane: BorderPane

    private val rezImageProvider: RezImageProvider

    init {
        rezImageProvider = CachingRezImageProvider(mainWindow.rezImageLoader)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        bind(newMenuItem, newButton, mainWindow.newAction)
        bind(openMenuItem, openButton, mainWindow.openAction)
        bind(saveMenuItem, saveButton, mainWindow.saveAction)
        bind(editButton, mainWindow.editAction)

        toolToggleGroup = toggleGroup(
                listOf(
                        selectToolButton to Tool.SELECT,
                        pencilToolButton to Tool.PENCIL
                ),
                mainWindow.tool
        )

        activePlaneComboBox.bind<Plane>(
                mainWindow.editor.map { it.world.planes },
                mainWindow.editor.map { it.activePlane }
        ) { it.name }

        hideOverflow(worldViewPane)

        worldViewPane.centerProperty().bind(mainWindow.editor.map { worldView(it) })
    }

    private fun worldView(editor: Editor): Node {
        return pane(editor.planeEditor.map { wapuniverse.view.worldView(it, rezImageProvider) })
    }
}

inline fun <reified T> toggleGroup(toggles: List<Pair<Toggle, T>>, observable: ObservableValue<Property<T>?>) =
        ToggleGroup().apply {
            this.toggles.addAll(toggles.map { (toggle, value) -> toggle.apply { userData = value } })
            observable.subscribe { newProperty ->
                if (newProperty == null) return@subscribe
                selectToggle(this.toggles.find { it.userData == newProperty.value })
                newProperty.bind(selectedToggleProperty().map { it.userData as T })
            }
        }
