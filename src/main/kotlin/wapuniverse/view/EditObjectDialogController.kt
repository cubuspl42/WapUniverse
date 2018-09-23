package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import wapuniverse.model.EditObjectDialog
import wapuniverse.view.extensions.map
import java.net.URL
import java.util.ResourceBundle

class EditObjectDialogController(
        private val editObjectDialog: EditObjectDialog
) : Initializable {
    @FXML
    lateinit var xTextField: TextField

    @FXML
    lateinit var yTextField: TextField

    @FXML
    lateinit var saveButton: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        xTextField.textProperty().value = editObjectDialog.x.value.toString()
        editObjectDialog.x.bind(xTextField.textProperty().map { it.toInt() })
        yTextField.textProperty().value = editObjectDialog.y.value.toString()
        editObjectDialog.y.bind(yTextField.textProperty().map { it.toInt() })
        saveButton.setOnAction { editObjectDialog.save() }
    }
}
