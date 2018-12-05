package wapuniverse.javafx

import wapuniverse.application.ApplicationPresenter
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class JavaFxApplicationPresenter(
        primaryStage: Stage
) : ApplicationPresenter {
    private val stageProvider = StageProvider(primaryStage)

    override fun createRootWindow(): JavaFxRootWindow {
        val stage = stageProvider.newStage().apply {
            title = "Root Window"
            scene = Scene(BorderPane(Label("Foo")), 320.0, 240.0)
            show()
        }
        return JavaFxRootWindow(stage)
    }
}
