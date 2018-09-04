package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import wapuniverse.model.MainWindow
import wapuniverse.view.util.bind
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        bind(newMenuItem, newButton, model.newAction)
        bind(openMenuItem, openButton, model.openAction)
        bind(saveMenuItem, saveButton, model.saveAction)
    }
}
