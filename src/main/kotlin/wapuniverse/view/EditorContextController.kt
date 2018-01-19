package wapuniverse.view

import javafx.beans.property.Property
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import org.fxmisc.easybind.EasyBind.listBind
import wapuniverse.model.EditorContext
import wapuniverse.model.Plane
import wapuniverse.model.SelectToolContext
import wapuniverse.model.Tool
import wapuniverse.model.selectToolContext
import wapuniverse.view.ext.map
import java.net.URL
import java.util.ResourceBundle
import javafx.scene.control.ListCell


class EditorContextController(
        private val worldPresenter: WorldPresenter,
        private val editorContext: EditorContext
) : Initializable {
    private val selectToolContext = editorContext.activePlaneContext
            .map { it?.selectToolContext as? SelectToolContext }

    @FXML
    lateinit var pane: BorderPane

    @FXML
    lateinit var imageSetText: Text

    val toggleGroup = ToggleGroup()

    @FXML
    lateinit var selectToolButton: ToggleButton

    @FXML
    lateinit var moveToolButton: ToggleButton

    @FXML
    lateinit var activePlaneComboBox: ComboBox<Plane>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        toggleGroup.apply {
            toggles.addAll(
                    selectToolButton,
                    moveToolButton
            )
            selectToggle(selectToolButton)
        }

        selectToolButton.userData = Tool.SELECT
        moveToolButton.userData = Tool.MOVE

        editorContext.activeTool.bind(toggleGroup.selectedToggleProperty().map { it.userData as Tool })

        listBind(activePlaneComboBox.items, editorContext.world.planes)
        activePlaneComboBox.valueProperty().bindBidirectional(editorContext.activePlane as Property<Plane>)
        activePlaneComboBox.setCellFactory {
            object : ListCell<Plane>() {
                override fun updateItem(plane: Plane?, bln: Boolean) {
                    super.updateItem(plane, bln)
                    plane?.let { textProperty().bind(it.name) }
                }
            }
        }
        activePlaneComboBox.buttonCell = activePlaneComboBox.cellFactory.call(null)

        pane.centerProperty().bind(editorContext.activePlaneContext.map {
            it?.let { worldPresenter.presentWorldView(it) }
        })
    }

    fun onDelete(actionEvent: ActionEvent) {
        selectToolContext.value?.deleteSelectedObjects()
    }
}
