package wapuniverse.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.Tool
import wapuniverse.view.ext.map
import java.net.URL
import java.util.ResourceBundle

class EditorContextController(
        private val worldPresenter: WorldPresenter,
        private val editorContext: EditorContext
) : Initializable {
    @FXML
    lateinit var pane: BorderPane

    @FXML
    lateinit var imageSetText: Text

    val toggleGroup = ToggleGroup()

    @FXML
    lateinit var selectToolButton: ToggleButton

    @FXML
    lateinit var moveToolButton: ToggleButton

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

        pane.center = worldPresenter.presentWorldView(editorContext)

        imageSetText.textProperty().bind(editorContext.world.objects.first().imageSet)
    }

    fun onDelete(actionEvent: ActionEvent) {
        (editorContext.activeToolContext.value as? SelectToolContext)?.deleteSelectedObjects()
    }
}
