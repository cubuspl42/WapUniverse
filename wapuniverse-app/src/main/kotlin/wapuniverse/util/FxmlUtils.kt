package wapuniverse.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import wapuniverse.app.MyApplication

fun loadFxml(fxmlFilename: String, function: () -> Any): Parent {
    val fxmlLoader = FXMLLoader(MyApplication::class.java.getResource(fxmlFilename)).apply {
        setControllerFactory { function() }
    }
    return fxmlLoader.load()
}
