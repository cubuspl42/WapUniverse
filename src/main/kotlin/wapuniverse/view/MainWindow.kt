package wapuniverse.view

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.model.MainWindow
import wapuniverse.view.util.loadFxml

private const val TITLE = "WapUniverse"
private const val MAIN_WINDOW_FXML = "/view/MainWindow.fxml"

fun mainWindow(stage: Stage, model: MainWindow) {
    stage.run {
        title = TITLE
        scene = Scene(mainWindowRoot(model))
        show()
    }
}

fun mainWindowRoot(model: MainWindow): Parent {
    return loadFxml(MAIN_WINDOW_FXML) {
        MainWindowController(model)
    }
}