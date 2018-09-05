package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import wapuniverse.model.BaseLevel
import wapuniverse.model.NewWorldDialog
import wapuniverse.view.extensions.bind
import wapuniverse.view.util.bind
import java.net.URL
import java.util.ResourceBundle

class NewWorldDialogController(private val model: NewWorldDialog) : Initializable {
    @FXML
    lateinit var baseLevelComboBox: ComboBox<BaseLevel>

    @FXML
    lateinit var createButton: Button

    @FXML
    lateinit var nameTextField: TextField


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        baseLevelComboBox.apply {
            items.setAll(*BaseLevel.values())
            bind(model.baseLevel, model.isOpen)
        }
        nameTextField.textProperty().bindBidirectional(model.name)
        bind(createButton, model.createAction)
    }
}
