package wapuniverse.view.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import wapuniverse.MyApplication

fun <TController> loadFxml(fxmlFilename: String, function: () -> TController): Parent {
    val fxmlLoader = FXMLLoader(MyApplication::class.java.getResource(fxmlFilename)).apply {
        setControllerFactory { function() }
    }
    return fxmlLoader.load()
}
