package wapuniverse.app

import javafx.scene.Scene
import javafx.stage.Stage

class RootWindow(
        stage: Stage
) {
    init {
        stage.apply {
            title = rootWindowTitle
            scene = Scene(rootWindowUi())
            show()
        }
    }
}
