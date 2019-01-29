package wapuniverse.javafx

import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import wapuniverse.javafx.util.StageProvider
import wapuniverse.viewmodel.Application
import wapuniverse.viewmodel.RootWindow

private const val windowTitle = "WapUniverse"

class ApplicationPresenter(
        primaryStage: Stage
) {
    private val stageProvider = StageProvider(primaryStage)

    fun presentApplication(application: Application) {
        application.onWindowCreated.subscribe { rootWindow ->
            presentRootWindow(rootWindow)
        }
    }

    private fun presentRootWindow(rootWindow: RootWindow) {
        newStage().apply {
            title = windowTitle
            scene = Scene(rootWindowRoot(rootWindow), 640.0, 480.0)
            show()
        }
    }

    private fun newStage() = stageProvider.newStage()
}

private fun rootWindowRoot(
        rootWindow: RootWindow
): Parent {
    val menuBar = MenuBar(
            Menu("File", null,
                    MenuItem("New"),
                    MenuItem("Open")
            )
    )
    val center = Group(

    )
    return BorderPane(
            center,
            menuBar,
            null,
            null,
            null
    )
}
